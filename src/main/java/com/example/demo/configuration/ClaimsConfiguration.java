package com.example.demo.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de <em>Swagger</em>.
 */
@Configuration
@ConfigurationProperties(prefix = "claims")
@Getter
@Setter
public class ClaimsConfiguration {

    private String role;
    private String login;
    private String issuer;
}
