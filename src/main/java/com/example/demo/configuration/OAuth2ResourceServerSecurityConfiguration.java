/*
 * Sébastien Leboucher
 */
package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFlux
@EnableWebFluxSecurity
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class OAuth2ResourceServerSecurityConfiguration {
    public static final String ROLE = "ROLE_";
    private final ClaimsConfiguration claimsConfiguration;

    public OAuth2ResourceServerSecurityConfiguration(ClaimsConfiguration claimsConfiguration) {
        this.claimsConfiguration = claimsConfiguration;
    }

    // POUR HTTP WEBFLUX
    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowedOriginPatterns(List.of("*"));
        corsConfig.addAllowedMethod("OPTIONS");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("GET");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/v2/api-docs").permitAll()
                .pathMatchers("/actuator").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/api/publish").permitAll()
                .anyExchange().permitAll()
                .and()
//                .oauth2ResourceServer(oauth2ResourceServer ->
//                        oauth2ResourceServer
//                                .jwt(jwt -> jwt.jwtAuthenticationConverter(getJwtAuthenticationConverter()))
//                )
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
                                .route("*").permitAll()
                                .anyRequest().permitAll()
                                .anyExchange().permitAll())
//                .jwt(jwtSpec -> jwtSpec.authenticationManager(jwtReactiveAuthenticationManager(
//                        ReactiveJwtDecoders.fromOidcIssuerLocation(claimsConfiguration.getIssuer()))))
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
