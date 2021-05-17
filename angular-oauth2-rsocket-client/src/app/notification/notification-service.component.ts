import { Injectable } from '@angular/core';
import { AuthService } from '../core/auth-service.component';
import { InitNotif } from './InitNotif';
import { ClientResume } from './ClientResume';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {ConfigurationService} from '../core/configuration.service';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    constructor(private authService: AuthService, private configurationService: ConfigurationService) {}

    subscribeSummerNotif(init: InitNotif): Observable<ClientResume> {
        return this.authService.getAccessToken()
            .pipe(map(accessToken => {
                return new ClientResume({
                    jwt: accessToken,
                    url: this.configurationService.getConfiguration().urlAlertDiffusion,
                    api: this.configurationService.getConfiguration().canalStreamAlertDiffusion,
                    parameters: init.parameters,
                    onNext: init.onNext,
                });
            }));
    }
}
