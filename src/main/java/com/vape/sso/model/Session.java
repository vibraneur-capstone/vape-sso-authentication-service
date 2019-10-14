package com.vape.sso.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Setter(AccessLevel.NONE)
@Document(collection="liveSessions")
public class Session {
    @Id
    private String sessionId;
    private String jwt;
    private String userId;
    private String userAgent;
    private String createdAt;
    private String expiresAt;
}
