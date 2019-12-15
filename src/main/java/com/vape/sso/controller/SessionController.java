package com.vape.sso.controller;

import com.vape.sso.service.TokenService;
import com.vape.sso.swagger.v1.api.TokenApi;
import com.vape.sso.swagger.v1.model.SessionRequest;
import com.vape.sso.swagger.v1.model.Token;
import com.vape.sso.swagger.v1.model.TokenState;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class SessionController implements TokenApi {

    @Autowired
    private TokenService tokenService;

    @Override
    public ResponseEntity<TokenState> getSession(String sessionId, String token) {
        //TODO remove this mock and implement real shit
//        TokenState mock = new TokenState();
//        mock.setStatus(SessionStatus.ACTIVE.to);
        return null;
    }

    @Override
    public ResponseEntity<Token> postNewSession(String clientId, SessionRequest credential) {
        if(StringUtils.isEmpty(clientId) || !clientId.equals(credential.getClientId())) {
            return new ResponseEntity<>(new Token(), HttpStatus.BAD_REQUEST);
        }
        Token session = tokenService.activateNewSession(credential);
        if(session.getSecrete() == null) {
            return new ResponseEntity<>(session, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(tokenService.activateNewSession(credential), HttpStatus.OK);
    }
}
