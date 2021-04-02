/*
 * Sébastien Leboucher
 */
package com.example.demo.configuration;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final ClaimsConfiguration claimsConfiguration;

    public AuthUtil(ClaimsConfiguration claimsConfiguration) {
        this.claimsConfiguration = claimsConfiguration;
    }

    public String getLogin(Jwt jwt) {
        return jwt.getClaimAsString(claimsConfiguration.getLogin());
    }
}
