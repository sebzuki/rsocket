import { RSocketClientHandler } from './RSocketClientHandler';

export class ClientResume {
    private handler: RSocketClientHandler = null;
    private status: string = null;

    constructor(private call: RSocketResumeRequest) {
        this.init(call);
    }

    private init(call: RSocketResumeRequest) {
        this.handler = new RSocketClientHandler({
            ...this.call,
            onSuccess: () => {
                // not implemented
            },
            cancelCallback: () => {
                // not implemented
            },
            onError: (e) => {
                this.reload();
            }
        });

        this.handler.getStatus().subscribe({
            onNext: status => {
                this.status = status.kind;
            },
            onSubscribe: subscription => {
                subscription.request(2147483647);
            },
        });

        this.handler.requestStream();
    }

    getStatus(): string {
        return this.status;
    }

    close() {
        this.handler.close();
    }

    private reload() {
        setTimeout(() => {
            try {
                this.handler.close();
            } finally {
                this.init(this.call);
            }
        }, 2000);
    }
}

export interface RSocketResumeRequest {
    jwt: string,
    url: string,
    api: string,
    parameters: any,
    onNext
}
