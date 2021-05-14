import {
    APPLICATION_JSON,
    BufferEncoders,
    encodeBearerAuthMetadata,
    encodeCompositeMetadata,
    encodeRoute,
    IdentitySerializer,
    JsonSerializer,
    MESSAGE_RSOCKET_AUTHENTICATION,
    MESSAGE_RSOCKET_COMPOSITE_METADATA,
    MESSAGE_RSOCKET_ROUTING,
    RSocketClient,
    RSocketResumableTransport,
    UTF8Encoder
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import type { Encodable } from 'rsocket-types';
import { ConnectionStatus } from 'rsocket-types';
import { Payload } from 'rsocket-types/ReactiveSocketTypes';
import { ISubscription } from 'rsocket-types/ReactiveStreamTypes';
import { Flowable } from 'rsocket-flowable';

export class RSocketClientHandler {
    private webSocketClient: RSocketResumableTransport = null;
    private client: RSocketClient<any, any> = null;
    private call: RSocketRequest = null;

    constructor(call: RSocketRequest) {
        this.call = call;

        this.webSocketClient = new RSocketWebSocketClient(
                {url: this.call.url},
                {
                    ...BufferEncoders,
                    data: UTF8Encoder,
                });

        this.client = new RSocketClient({
            serializers: {
                data: JsonSerializer,
                metadata: IdentitySerializer
            },
            setup: {
                keepAlive: 60000,
                lifetime: 180000,
                dataMimeType: APPLICATION_JSON.string,
                metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string,
                payload: {
                    metadata: encodeCompositeMetadata([
                        [MESSAGE_RSOCKET_AUTHENTICATION, encodeBearerAuthMetadata(this.call.jwt)],
                    ])
                },
            },
            transport: this.webSocketClient,
            errorHandler: this.call.onError
        });
    }

    public getStatus(): Flowable<ConnectionStatus> {
        return this.webSocketClient.connectionStatus();
    }

    public requestStream(): void {
        this.client.connect().subscribe({
            onComplete: socket => {
                socket.requestStream({
                    data: this.call.parameters,
                    metadata: encodeCompositeMetadata([
                        [MESSAGE_RSOCKET_ROUTING, encodeRoute(this.call.api)]
                    ])
                }).subscribe({
                    onNext: (payload: Payload<any, Encodable>) => this.call.onNext(payload.data),
                    onSubscribe: (subscription: ISubscription) => {
                        this.call.onSuccess(true);
                        subscription.request(2147483647);
                    },
                    onError: this.call.onError,
                })
            },
            onError: this.call.onError,
            onSubscribe: cancel => this.call.cancelCallback(cancel),
        })
    }

    public close() {
        this.client?.close();
        this.webSocketClient?.close();
    }
}

export interface RSocketRequest {
    jwt: string,
    url: string,
    api: string,
    parameters: any,
    onNext,
    onError,
    onSuccess,
    cancelCallback
}
