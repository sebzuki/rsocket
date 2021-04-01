import { Profile } from 'oidc-client';
import { AccessToken } from './access-token';

export class TokensDecoded {
    idToken: Profile;
    accessToken: AccessToken;

    constructor(idToken: Profile, accessToken: AccessToken) {
        this.idToken = { ...idToken };
        this.accessToken = { ...accessToken };
    }
}