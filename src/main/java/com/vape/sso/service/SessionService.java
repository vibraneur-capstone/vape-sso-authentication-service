package com.vape.sso.service;

import com.vape.sso.model.Session;
import com.vape.sso.repository.SessionRepository;
import com.vape.sso.swagger.model.SessionRequest;
import com.vape.sso.swagger.model.SessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public SessionResponse activateNewSession(SessionRequest request){
        //TODO remove this mock and implement real shit
        sessionRepository.save(Session.builder().jwt("dummyJWTForTest").userAgent("Test").createdAt("2019").expiresAt("2018").userId("2018").build());
        SessionResponse mock = new SessionResponse();
        mock.setSecrete("this is a mock secret");
        mock.setSessionId("this is a mock id");
        return mock;
    }
}
