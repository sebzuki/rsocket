import {Component} from '@angular/core';
import {User} from "./User";
import {IdentitySerializer, JsonSerializer, RSocketClient} from 'rsocket-core';
import RSocketWebSocketClient from "rsocket-websocket-client";
import {ISubscription} from "rsocket-types/ReactiveStreamTypes";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  users: User[] = [];
  path: string = 'findAll';
  url: string = 'ws://localhost:7000/rsocket';

  constructor() {
    const errorHanlder = (e: any) => console.log(e);
    new RSocketClient({
      serializers: {
        data: JsonSerializer,
        metadata: IdentitySerializer
      },
      setup: {
        keepAlive: 20000,
        lifetime: 180000,
        dataMimeType: 'application/json',
        metadataMimeType: 'message/x.rsocket.routing.v0',
      },
      transport: new RSocketWebSocketClient({url: this.url}),
    })
      .connect()
      .then(socket => {
        socket.requestStream({data: undefined, metadata: String.fromCharCode(this.path.length) + this.path})
          .subscribe({
            onError: errorHanlder,
            onNext: (payload: any) => {
              this.users.push(payload.data)
            },
            onSubscribe: (subscription: ISubscription) => {
              subscription.request(10000); // set it to some max value
            }
          })
      }, errorHanlder);
  }
}
