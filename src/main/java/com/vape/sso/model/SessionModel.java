package com.vape.sso.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@Setter(AccessLevel.NONE)
@Document(collection="liveSessions")
public class SessionModel {
    @Id
    private String sessionId;
    private String jwt;
    private String user;
    private String userAgent;
    private LocalTime createdTime;
}
