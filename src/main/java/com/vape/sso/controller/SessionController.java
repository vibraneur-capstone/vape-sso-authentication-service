package com.vape.sso.controller;

import com.vape.sso.service.SessionService;
import com.vape.sso.swagger.model.SessionRequest;
import com.vape.sso.swagger.model.SessionResponse;
import com.vape.sso.swagger.model.SessionState;
import com.vape.sso.swagger.model.SessionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @RequestMapping(
            method={RequestMethod.GET},
            value = {"/{sessionId}/secret/{secret}"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public SessionState checkSession(@PathVariable(value="sessionId", required = true) String sessionId,
                                     @PathVariable(value="secret", required = true) String secret){
        //TODO remove this mock and implement real shit
        SessionState mock = new SessionState();
        mock.setStatus(SessionStatus.ACTIVE);
        return mock;
    }

    @RequestMapping(
            method={RequestMethod.POST},
            value = "",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public SessionResponse activateSession(@RequestBody @Valid SessionRequest request) {
        //TODO : set cookie in header
        return sessionService.activateNewSession(request);
    }
}
