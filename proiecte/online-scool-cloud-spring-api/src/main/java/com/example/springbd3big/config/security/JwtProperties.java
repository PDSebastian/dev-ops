package com.example.springbd3big.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "b25saW5lLXNjaG9vbC1jbG91ZC1hcGktc3VwZXItc2VjdXJlLWp3dC1zZWNyZXQtbXVzdC1iZS1sb25nLWVub3VnaA==";
    private long expirationSeconds = 86400;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationSeconds() {
        return expirationSeconds;
    }

    public void setExpirationSeconds(long expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }
}
