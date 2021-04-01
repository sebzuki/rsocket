/*
 * Sébastien Leboucher
 */
package com.example.demo.configuration;

import org.springframework.security.oauth2.jwt.Jwt;

public class ClaimsUtil {

    public static String getLogin(Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }
}
