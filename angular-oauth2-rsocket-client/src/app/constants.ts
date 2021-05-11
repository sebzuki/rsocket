// link with ENV parameters here
export class Constants {
	public static clientRoot = 'http://localhost:4200/';
	public static oidcProvider = 'https://sso.aron-lab.fr/auth/realms/msa/.well-known/openid-configuration';
	public static clientId = 'spa';
	public static scope  ='openid profile email';
	public static responseType  ='code';
	public static clientSecret = null;
	public static metadata = null;

	// SET metadata only if openid discovery configuration not working
	// public static metadata = {
	// 	issuer: 'http://192.168.1.15:4444/',
	// 	authorization_endpoint: 'http://192.168.1.15:4444/oauth2/auth',
	// 	userinfo_endpoint: 'http://192.168.1.15:4444/userinfo',
	// 	end_session_endpoint: 'http://192.168.1.15:4444/oauth2/token',
	// 	jwks_uri: 'http://192.168.1.15:4444/.well-known/jwks.json',
	// 	token_endpoint: 'http://192.168.1.15:4444/oauth2/token'
	// }
}
