import { Injectable } from '@angular/core';
import { AuthService } from '../core/auth-service.component';
import { InitNotif } from './InitNotif';
import { ClientResume } from './ClientResume';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private notifServerUrl: string = 'ws://localhost:7000/rsocket';

    constructor(private _authService: AuthService) {}

    subscribeSummerNotif(init: InitNotif): Observable<ClientResume> {
        return this._authService.getAccessToken()
            .pipe(map(accessToken => {
                return new ClientResume({
                    jwt: accessToken,
                    url: this.notifServerUrl,
                    api: 'notif',
                    parameters: init.parameters,
                    onNext: init.onNext,
                });
            }));
    }
}
