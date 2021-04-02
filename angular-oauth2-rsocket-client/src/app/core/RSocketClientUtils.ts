import {
    APPLICATION_JSON,
    BufferEncoders,
    ClientConfig,
    encodeBearerAuthMetadata,
    encodeCompositeMetadata,
    encodeRoute,
    IdentitySerializer,
    JsonSerializer,
    MESSAGE_RSOCKET_AUTHENTICATION,
    MESSAGE_RSOCKET_COMPOSITE_METADATA,
    MESSAGE_RSOCKET_ROUTING,
    RSocketClient,
    UTF8Encoder
} from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';
import type { Encodable } from 'rsocket-types';
import { Payload } from 'rsocket-types/ReactiveSocketTypes';
import { ISubscription } from 'rsocket-types/ReactiveStreamTypes';

export class RSocketClientUtils {
    public static clientConfig(jwt: string, url: string): ClientConfig<any, Encodable> {
        return {
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
                        [ MESSAGE_RSOCKET_AUTHENTICATION, encodeBearerAuthMetadata(jwt) ],
                    ])
                },
            },
            transport: new RSocketWebSocketClient(
                { url },
                {
                    ...BufferEncoders,
                    data: UTF8Encoder,
                })
        }
    }

    public static requestStream(call: RSocketRequest): void {
        new RSocketClient(this.clientConfig(call.jwt, call.url)).connect().subscribe({
            onComplete: socket => {
                socket.requestStream({
                    data: call.parameters,
                    metadata: encodeCompositeMetadata([
                        [ MESSAGE_RSOCKET_ROUTING, encodeRoute(call.api) ]
                    ])
                }).subscribe({
                    onComplete: () => console.info('onComplete'),
                    onNext: (payload: Payload<any, Encodable>) => call.onNext(payload.data),
                    onSubscribe: (subscription: ISubscription) => {
                        call.onSuccess(true);
                        subscription.request(1000000); // set it to some max value
                    },
                    onError: call.onError,
                })
            },
            onError: call.onError,
            onSubscribe: cancel => call.cancelCallback(cancel)
        })
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
