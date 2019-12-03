package com.vape.sso.controller;

import com.vape.sso.service.SessionService;
import com.vape.sso.swagger.v1.api.SessionApi;
import com.vape.sso.swagger.v1.model.SessionRequest;
import com.vape.sso.swagger.v1.model.SessionResponse;
import com.vape.sso.swagger.v1.model.SessionState;
import com.vape.sso.type.SessionStatus;
import io.swagger.annotations.ApiParam;
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

    @RequestMapping(
            method={RequestMethod.GET},
            value = {"/session/{sessionId}/token"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SessionState> checkSession(@PathVariable(value="sessionId", required = true) String sessionId,
                                                     @NotNull @Valid @RequestParam(value = "secret", required = true) String secret){
        //TODO remove this mock and implement real shit
        SessionState mock = new SessionState();
//        mock.setStatus(SessionStatus.ACTIVE.to);
        return null;
    }

    @RequestMapping(
            method={RequestMethod.POST},
            value = "/session/clientId/{clientId}",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SessionResponse> getNewSession(@PathVariable(value="clientId", required = true) String sessionId,
                                                         @RequestBody @Valid SessionRequest request) {
        if(StringUtils.isEmpty(sessionId) || !sessionId.equals(request.getClientId())){
            return new ResponseEntity<>(new SessionResponse().sessionId("").secrete(""), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(sessionService.activateNewSession(request), HttpStatus.OK);
    }
}
