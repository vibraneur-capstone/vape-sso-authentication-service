package com.vape.sso.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Setter(AccessLevel.NONE)
@Document(collection="audit")
public class AuditSession {
    @Id
    private String auditId;
    private String jwt;
    private String userId;
    private String userAgent;
    private String sessionStartDate;
    private String sessionEndDate;
}
