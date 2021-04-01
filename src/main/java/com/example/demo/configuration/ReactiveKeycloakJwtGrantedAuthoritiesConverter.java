/*
 * Sébastien Leboucher
 */
package com.example.demo.configuration;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ReactiveKeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {
    private static final String DEFAULT_AUTHORITY_PREFIX = "SCOPE_";

    private String authorityPrefix = DEFAULT_AUTHORITY_PREFIX;
    private String authoritiesClaimName;

    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        return Flux.fromStream(getAuthorities(jwt).stream()
                .map(authority -> new SimpleGrantedAuthority(this.authorityPrefix + authority)));
    }

    public void setAuthorityPrefix(String authorityPrefix) {
        Assert.hasText(authorityPrefix, "authorityPrefix cannot be empty");
        this.authorityPrefix = authorityPrefix;
    }

    public void setAuthoritiesClaimName(String authoritiesClaimName) {
        Assert.hasText(authoritiesClaimName, "authoritiesClaimName cannot be empty");
        this.authoritiesClaimName = authoritiesClaimName;
    }

    private Collection<String> getAuthorities(Jwt jwt) {

        if (this.authoritiesClaimName == null) {
            return Collections.emptyList();
        }

        Object authorities = jwt.getClaim(this.authoritiesClaimName);
        if (authorities instanceof JSONObject) {
            return ((JSONObject) authorities).values().stream()
                    .map(roles -> Arrays.asList(((JSONArray) roles).toArray()))
                    .flatMap(Collection::stream)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
