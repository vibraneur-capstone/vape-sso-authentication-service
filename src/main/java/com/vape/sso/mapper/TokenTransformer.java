package com.vape.sso.mapper;

import com.vape.sso.model.TokenModel;
import com.vape.sso.swagger.v1.model.Token;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class TokenTransformer {
    /**
     * mapper method for mapping SessionModel to Token
     *
     * @param session SessionModel
     * @return Token
     */
    public Token toToken(TokenModel session) {
        Token response = new Token();
        response.id(session.getId());
        response.jwt(session.getJwt());
        return response;
    }
}
