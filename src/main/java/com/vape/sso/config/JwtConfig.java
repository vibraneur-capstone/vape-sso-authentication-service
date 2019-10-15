package com.vape.sso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("com.vape.sso.jwt")
public class JwtConfig {
    private String issuer;
    private String signatureAlgorithm;
    private long maxAge;
    private String secretKey;
}
