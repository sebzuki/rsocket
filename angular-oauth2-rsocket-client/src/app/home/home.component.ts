import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../core/auth-service.component';
import { UserProfile } from '../oidc/model/user-profile';
import { HttpClient } from '@angular/common/http';
import { Subscription } from "rxjs";
import { Notif } from './Notif';
import { RSocketClientUtils } from '../core/RSocketClientUtils';

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html'
})
export class HomeComponent implements OnInit, OnDestroy {
    userProfile: UserProfile;
    permissions: string[];
    notifs: Notif[] = [];
    url: string = 'ws://localhost:7000/rsocket';
    subscriptionToken: Subscription;

    constructor(private _authService: AuthService, private _httpClient: HttpClient) {
    }

    ngOnDestroy(): void {
        this.subscriptionToken.unsubscribe();
    }

    ngOnInit() {
        this.userProfile = this._authService.getUserProfile();
        this.permissions = this.userProfile?.userPermissions.map(p => p.role);

        this.subscriptionToken = this._authService.getAccessToken().subscribe(accessToken =>
            RSocketClientUtils.requestStream({
                jwt: accessToken,
                url: this.url,
                api: 'stream',
                parameters: {
                    id: '123e4567-e89b-12d3-a456-426614174000',
                    code: 'NH2-1'
                },
                onNext: (data: Notif) => {
                    if (data) {
                        this.notifs.push(data)
                    }
                },
                cancelCallback: (cancel) => {
                    // call cancel to stop
                }
            })
        );
    }
}
