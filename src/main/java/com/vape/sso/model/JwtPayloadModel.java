package com.vape.sso.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vape.sso.type.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@Setter(AccessLevel.NONE)
public class JwtPayloadModel {
    private String user;
    private String fullName;
    private String userRoles;
    private String userAgent;
    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String toString(){
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
