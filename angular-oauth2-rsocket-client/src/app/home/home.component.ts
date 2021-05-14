import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService } from '../core/auth-service.component';
import { UserProfile } from '../oidc/model/user-profile';
import { HttpClient } from '@angular/common/http';
import { Observable, Subscription } from 'rxjs';
import { Notif } from './Notif';
import { RSocketClientResume } from '../core/RSocketClientResume';

@Component({
    selector: 'app-home',
    templateUrl: 'home.component.html'
})
export class HomeComponent implements OnInit, OnDestroy, AfterViewInit {
    userProfile: UserProfile;
    permissions: string[];
    notifs: Notif[] = [];
    url: string = 'ws://localhost:7000/rsocket';
    subscriptionToken: Subscription;
    error: string = 'Not initialized';
    success: boolean = false;
    number$: Observable<number>;
    utils: RSocketClientResume = null

    constructor(private _authService: AuthService, private _httpClient: HttpClient) {
    }

    ngAfterViewInit(): void {
        this.loadSubscribers();
    }

    ngOnDestroy(): void {
        this.subscriptionToken.unsubscribe();
        this.utils?.close();
    }

    ngOnInit() {
        this.userProfile = this._authService.getUserProfile();
        this.permissions = this.userProfile?.userPermissions.map(p => p.role);

        // this.loadAppelCascade().subscribe(value => console.log(value));

        this.subscriptionToken = this._authService.getAccessToken().subscribe(accessToken => {
            this.utils = new RSocketClientResume({
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
            });
        });
    }

    loadSubscribers(): void {
        // this.number$ = this._httpClient.get<number>('http://localhost:8082/api/subscribers');
    }

    loadAppelCascade(): Observable<any> {
        return this._httpClient.get<number>('http://localhost:8082/api/ext');
    }
}
