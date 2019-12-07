package com.vape.sso.controller;

import com.vape.sso.service.SessionService;
import com.vape.sso.swagger.v1.api.SessionApi;
import com.vape.sso.swagger.v1.model.SessionRequest;
import com.vape.sso.swagger.v1.model.SessionResponse;
import com.vape.sso.swagger.v1.model.SessionState;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class SessionController implements SessionApi {

    @Autowired
    private SessionService sessionService;

    @Override
    public ResponseEntity<SessionState> getSession(String sessionId, String token) {
        //TODO remove this mock and implement real shit
//        SessionState mock = new SessionState();
//        mock.setStatus(SessionStatus.ACTIVE.to);
        return null;
    }

    @Override
    public ResponseEntity<SessionResponse> postNewSession(String clientId, SessionRequest credential) {
        if (StringUtils.isEmpty(clientId) || !clientId.equals(credential.getClientId())) {
            return new ResponseEntity<>(new SessionResponse(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(sessionService.activateNewSession(credential), HttpStatus.OK);
    }
}
