package com.vape.sso.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Setter(AccessLevel.NONE)
@Document(collection="salt")
public class CredentialSaltModel {
    @Id
    private String id;
    private String associateId;
    private String salt;
}
