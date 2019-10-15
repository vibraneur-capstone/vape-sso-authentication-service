package com.vape.sso.model;

import com.vape.sso.type.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Setter(AccessLevel.NONE)
@Document(collection="users")
public class UserModel {
    @Id
    private String id;
    private String clientId;
    private String fullName;
    private String hashedPwd;
    private List<UserRole> userRole;
}
