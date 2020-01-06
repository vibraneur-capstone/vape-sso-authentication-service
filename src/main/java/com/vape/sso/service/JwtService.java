package com.vape.sso.service;

import com.vape.sso.config.JwtConfig;
import com.vape.sso.model.JwtPayloadModel;
import com.vape.sso.model.UserModel;
import com.vape.sso.swagger.v1.model.TokenRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * Check if a jwt token is valid
     * @param jwt
     * @return boolean
     */
    boolean isValidJWT(String jwt) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.valueOf(jwtConfig.getSignatureAlgorithm());
            Key signingKey = getSigningKey(signatureAlgorithm);
            Jws jwtClaims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
            return jwtClaims != null;
        } catch (SignatureException | ExpiredJwtException e) {
            return false;
        }
    }

    /**
     * generate a jwt token from payload and subject, also set expire time
     * @param payload
     * @param subject
     * @return string jwt
     */
    String createJWT(JwtPayloadModel payload, String subject) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.valueOf(jwtConfig.getSignatureAlgorithm());

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        Key signingKey = getSigningKey(signatureAlgorithm);

        //build JWT content
        JwtBuilder builder = Jwts.builder()
                .setId(generateJwtId(payload.toString(), subject))
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

    // create signing key
    private Key getSigningKey(SignatureAlgorithm signatureAlgorithm) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtConfig.getSecretKey());
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    private String generateJwtId(String payload, String subject) {
        return DigestUtils.sha256Hex(String.format("%s%s", payload, subject));
    }

    /**
     * generate jwt payload content
     *
     * @param user         UserModel
     * @param tokenRequest TokenRequest
     * @return JwtPayloadModel
     */
    JwtPayloadModel generatePayload(UserModel user, TokenRequest tokenRequest) {
        return JwtPayloadModel.builder()
                .fullName(user.getFullName())
                .user(user.getClientId())
                .userRoles(user.getUserRole().toString())
                .userAgent(tokenRequest.getUserAgent())
                .build();
    }

}
