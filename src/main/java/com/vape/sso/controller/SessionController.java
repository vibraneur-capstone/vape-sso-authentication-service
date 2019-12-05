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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/")
public class SessionController implements SessionApi {

    @Autowired
    private SessionService sessionService;

    @Override
    @RequestMapping(
            value = {"/session/client"},
            produces = {"application/json"},
            method = {RequestMethod.GET})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SessionState> checkSession(@NotNull @Valid @RequestParam(value = "sessionId") String sessionId,
                                                     @NotNull @Valid @RequestParam(value = "token") String token) {

        //TODO remove this mock and implement real shit
//        SessionState mock = new SessionState();
//        mock.setStatus(SessionStatus.ACTIVE.to);
        return null;
    }

    @Override
    @RequestMapping(
            value = {"/session/clientId/{clientId}"},
            produces = {"application/json"},
            consumes = {"application/json"},
            method = {RequestMethod.POST})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SessionResponse> getNewSession(@PathVariable("clientId") String clientId,
                                                         @Valid @RequestBody SessionRequest credential) {
        if (StringUtils.isEmpty(clientId) || !clientId.equals(credential.getClientId())) {
            return new ResponseEntity<>(new SessionResponse().sessionId("").secrete(""), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(sessionService.activateNewSession(credential), HttpStatus.OK);
    }
}
