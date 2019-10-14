package com.vape.sso.model;

import com.vape.sso.type.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
@Document(collection="users")
public class User {
    @Id
    private String id;
    private String user;
    private String hashedPwd;
    private UserRole userRole;
}
