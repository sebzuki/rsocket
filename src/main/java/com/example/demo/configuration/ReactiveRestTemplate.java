/*
 * Sébastien Leboucher
 */
package com.example.demo.configuration;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@Component
public class ReactiveRestTemplate {

    public Mono<RestTemplate> getSecureTemplate() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> (Jwt) authentication.getPrincipal())
                .map(jwt -> {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getInterceptors().add((request, body, clientHttpRequestExecution) -> {
                        request.getHeaders().setBearerAuth(jwt.getTokenValue());
                        return clientHttpRequestExecution.execute(request, body);
                    });
                    return restTemplate;
                });
    }
}
