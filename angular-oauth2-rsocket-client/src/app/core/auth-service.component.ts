import { Injectable } from '@angular/core';
import { User, UserManager } from 'oidc-client';
import { combineLatest, from, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import jwt_decode from "jwt-decode";

import { Constants } from '../constants';
import { AuthContext } from '../oidc/model/auth-context';
import { UserProfile } from '../oidc/model/user-profile';
import { TokensDecoded } from '../oidc/model/tokens-decoded';
import { UserPermission } from '../oidc/model/user-permission';


@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private _userManager: UserManager;
    private _user: User;
    private _loginChangedSubject = new Subject<boolean>();

    loginChanged = this._loginChangedSubject.asObservable();
    authContext: AuthContext;

    constructor() {
        this._userManager = new UserManager({
            authority: Constants.oidcProvider,
            client_id: Constants.clientId,
            redirect_uri: `${Constants.clientRoot}signin-callback`,
            scope: Constants.scope,
            response_type: Constants.responseType,
            automaticSilentRenew: true,
            post_logout_redirect_uri: `${Constants.clientRoot}signout-callback`,
            silent_redirect_uri: `${Constants.clientRoot}assets/silent-callback.html`,
            loadUserInfo: false, // as it is pass with id token
            metadata: Constants.metadata,
            client_secret: Constants.clientSecret
        });

        this._userManager.events.addAccessTokenExpired(_ => {
            this._loginChangedSubject.next(false);
        });

        this._userManager.events.addUserLoaded(user => {
            if (this._user !== user) {
                this._user = user;
                this.loadSecurityContext();
                this._loginChangedSubject.next(this.validUser(user));
            }
        });

    }

    login() {
        return this._userManager.signinRedirect();
    }

    isLoggedIn(): Promise<boolean> {
        return this._userManager.getUser().then(user => {
            const userCurrent = this.validUser(user);
            if (this._user !== user) {
                this._loginChangedSubject.next(userCurrent);
            }
            if (userCurrent && !this.authContext) {
                this.loadSecurityContext();
            }
            this._user = user;
            return userCurrent;
        });
    }

    completeLogin() {
        return this._userManager.signinRedirectCallback().then(user => {
            this._user = user;
            this._loginChangedSubject.next(this.validUser(user));
            return user;
        });
    }

    logout() {
        this._userManager.signoutRedirect();
    }

    completeLogout() {
        this._user = null;
        this._loginChangedSubject.next(false);
        return this._userManager.signoutRedirectCallback();
    }

    getIdToken(): Observable<string> {
        return from(this._userManager.getUser().then(user => {
            if (this.validUser(user)) {
                return user.id_token;
            } else {
                return null;
            }
        }));
    }

    getAccessToken(): Observable<string> {
        return from(this._userManager.getUser().then(user => {
            if (this.validUser(user)) {
                return user.access_token;
            } else {
                return null;
            }
        }));
    }

    loadSecurityContext() {
        combineLatest([ this.getIdToken(), this.getAccessToken() ]).pipe(
            map(([ idToken, accessToken ]) =>
                new TokensDecoded(jwt_decode(idToken), jwt_decode(accessToken)))
        ).subscribe(context => {
            this.authContext = new AuthContext();
            this.authContext.targetAudience = context.accessToken.aud;
            this.authContext.userProfile = {
                login: context.idToken.preferred_username,
                id: context.idToken.sub,
                email: context.idToken.email,
                firstName: context.idToken.given_name,
                lastName: context.idToken.family_name,
                // map only client id permissions
                userPermissions: this.mapKeycloakUserPermissions(context.accessToken)
            };
        });
    }

    // specific to keycloak
    mapKeycloakUserPermissions(ressources: any): UserPermission[] {
        const clientAccess = ressources.mapped_realm_access; // map role of current client
        if (clientAccess) {
            return  clientAccess.roles.map(value => new UserPermission(value)); // map all roles
        } else {
            return [];
        }
    }

    getUserProfile(): UserProfile {
        if (this.authContext) {
            return this.authContext?.userProfile;
        } else {
            this._userManager.signinRedirect();
        }
    }

    userName(): string {
        const userProfile = this.getUserProfile();
        if (userProfile) {
            return `${userProfile.firstName} ${userProfile.lastName}`;
        }
        return '';
    }

    validUser(user: User): boolean {
        return !!user && !user.expired;
    }
}
