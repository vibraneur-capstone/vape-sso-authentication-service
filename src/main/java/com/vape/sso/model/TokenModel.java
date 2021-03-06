package com.vape.sso.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Data
@Builder
@Setter(AccessLevel.NONE)
@Document(collection="liveSessions")
public class TokenModel {
    @Id
    private String id;
    private String jwt;
    private String user;
    private String userAgent;
    private LocalTime createdTime;
}
