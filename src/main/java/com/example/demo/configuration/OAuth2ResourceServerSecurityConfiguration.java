/*
 * Sébastien Leboucher
 */
package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class OAuth2ResourceServerSecurityConfiguration implements WebFluxConfigurer {
    public static final String ROLE = "ROLE_";
    private final ClaimsConfiguration claimsConfiguration;

    public OAuth2ResourceServerSecurityConfiguration(ClaimsConfiguration claimsConfiguration) {
        this.claimsConfiguration = claimsConfiguration;
    }

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("PUT", "GET", "POST", "OPTION")
                .maxAge(3600);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, clientHttpRequestExecution) -> {
            Jwt accessToken = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            request.getHeaders().setBearerAuth(accessToken.getTokenValue());
            return clientHttpRequestExecution.execute(request, body);
        });

        return restTemplate;
    }

    // POUR HTTP WEBFLUX
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/**").permitAll()
                .and()
                .oauth2ResourceServer(oauth2ResourceServer ->
                        oauth2ResourceServer.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter()))
                )
                .build();
    }

    private Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> getJwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        ReactiveKeycloakJwtGrantedAuthoritiesConverter converter = new ReactiveKeycloakJwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(ROLE);
        converter.setAuthoritiesClaimName(claimsConfiguration.getRole());
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtAuthenticationConverter;
    }


    // POUR RSOCKET
    @Bean
    RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

    @Bean
    PayloadSocketAcceptorInterceptor authorization(RSocketSecurity rsocket) {
        return rsocket
                .authorizePayload(authorize ->
                        authorize
                                .route("*").authenticated()
                                .anyRequest().authenticated()
                                .anyExchange().authenticated())
                .jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager(
                        ReactiveJwtDecoders.fromOidcIssuerLocation(claimsConfiguration.getIssuer()))))
                .build();
    }

    private JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager(ReactiveJwtDecoder reactiveJwtDecoder) {
        JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager = new JwtReactiveAuthenticationManager(reactiveJwtDecoder);
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        ReactiveKeycloakJwtGrantedAuthoritiesConverter converter = new ReactiveKeycloakJwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(ROLE);
        converter.setAuthoritiesClaimName(claimsConfiguration.getRole());
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        jwtReactiveAuthenticationManager.setJwtAuthenticationConverter(jwtAuthenticationConverter);

        return jwtReactiveAuthenticationManager;
    }
}
