package com.vape.sso.service;

import com.vape.sso.config.JwtConfig;
import com.vape.sso.model.JwtPayloadModel;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import javax.xml.bind.DatatypeConverter;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    String createJWT(JwtPayloadModel payload, String subject) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.valueOf(jwtConfig.getSignatureAlgorithm());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // create signing key
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //build JWT content
        JwtBuilder builder = Jwts.builder()
                .setId(generateId(payload.toString(), subject))
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .claim("full-name", payload.getFullName())
                .claim("client-id", payload.getUser())
                .claim("user-agent", payload.getUserAgent())
                .claim("user-roles", payload.getUserRoles())
                .signWith(signatureAlgorithm, signingKey);

        //Add expiration
        if (jwtConfig.getMaxAge() >= 0) {
            long expMillis = nowMillis + jwtConfig.getMaxAge();
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    private String generateId(String payload, String subject) {
        return DigestUtils.sha256Hex(String.format("%s%s", payload, subject));
    }

}
