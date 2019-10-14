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

    /**
     * Activate a new session or update existing one with new jwt token
     * if request is not valid, an empty object is returned
     * @param request SessionRequest
     * @return SessionResponse
     */
    public SessionResponse activateNewSession(SessionRequest request) {
        return userService.validateSessionRequest(request)
                ? toSessionResponse(activate(request))
                : new SessionResponse();
    }

    private SessionModel activate(SessionRequest request) {
        SessionModel existing = sessionRepository.findSessionModelByUserName(request.getUser());
        return existing == null
                ? sessionRepository.save(generateNewSession(request))
                : sessionRepository.save(updateExistingSession(existing));
    }

    /**
     * Update SessionModel from existing model
     *
     * @param session SessionModel
     * @return SessionModel
     */
    private SessionModel updateExistingSession(SessionModel session) {
        return SessionModel.builder()
                .sessionId(session.getSessionId())
                .createdDate(session.getCreatedDate())
                .createdTime(session.getCreatedTime())
                .jwt(jwtService.createJWT("test id update", "test issuer update", "test subject update", 100000))
                .userName(session.getUserName())
                .build();
    }

    /**
     * Create a new SessionModel from SessionRequest
     *
     * @param request SessionRequest
     * @return SessionModel
     */
    private SessionModel generateNewSession(SessionRequest request) {
        return SessionModel.builder()
                .createdDate(LocalDate.now())
                .createdTime(LocalTime.now())
                .jwt(jwtService.createJWT("test id", "test issuer", "test subject", 100000))
                .userName(request.getUser())
                .build();
    }

    /**
     * mapper method for mapping SessionModel to SessionResponse
     *
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
