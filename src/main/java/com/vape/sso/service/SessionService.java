package com.vape.sso.service;

import com.vape.sso.model.SessionModel;
import com.vape.sso.repository.credential.SessionRepository;
import com.vape.sso.swagger.model.SessionRequest;
import com.vape.sso.swagger.model.SessionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public SessionResponse activateNewSession(SessionRequest request){
        if(userService.validateSessionRequest(request)) {
            SessionModel session = sessionRepository.save(generateNewSession(request));
            return toSessionResponse(session);
        }
        else {
            return new SessionResponse();
        }
    }

    /**
     * Create a new SessionModel
     * @param request SessionRequest
     * @return SessionModel
     */
    private SessionModel generateNewSession(SessionRequest request) {
        return SessionModel.builder()
                .createdDate(LocalDate.now())
                .createdTime(LocalTime.now())
                .jwt(jwtService.createJWT("test id", "test issuer", "test subject", 100000))
                .userId(request.getUser())
                .build();
    }

    /**
     * mapper method for mapping SessionModel to SessionResponse
     * @param session SessionModel
     * @return SessionResponse
     */
    private SessionResponse toSessionResponse(SessionModel session) {
        SessionResponse response = new SessionResponse();
        response.setSessionId(session.getSessionId());
        response.setSecrete(session.getJwt());
        return response;
    }
}
