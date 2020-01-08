package com.vape.sso.controller;

import com.vape.sso.service.TokenService;
import com.vape.sso.swagger.v1.api.TokenApi;
import com.vape.sso.swagger.v1.model.TokenRequest;
import com.vape.sso.swagger.v1.model.Token;
import com.vape.sso.swagger.v1.model.TokenState;
import com.vape.sso.swagger.v1.model.TokenStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin()
public class TokenController implements TokenApi {

    @Autowired
    private TokenService tokenService;

    @Override
    public ResponseEntity<TokenState> getSession(String tokenId, String token) {
        if(StringUtils.isEmpty(tokenId) || StringUtils.isEmpty(token)) {
            return new ResponseEntity<>(new TokenState(), HttpStatus.BAD_REQUEST);
        }
        TokenState tokenState = tokenService.getTokenState(tokenId, token);
        return tokenState.getStatus() == TokenStatus.ACTIVE ?
                new ResponseEntity<>(tokenState, HttpStatus.OK) :
                new ResponseEntity<>(tokenState, HttpStatus.UNAUTHORIZED);
    }

    @Override
    public ResponseEntity<Token> postNewSession(String clientId, TokenRequest credential) {
        if(StringUtils.isEmpty(clientId) || !clientId.equals(credential.getClientId())) {
            return new ResponseEntity<>(new Token(), HttpStatus.BAD_REQUEST);
        }
        Token session = tokenService.activateNewSession(credential);
        if(session == null) {
            return new ResponseEntity<>(new Token(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(session, HttpStatus.OK);
    }
}
