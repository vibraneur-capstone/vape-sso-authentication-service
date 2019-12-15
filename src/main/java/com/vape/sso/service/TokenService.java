package com.vape.sso.service;

import com.vape.sso.model.JwtPayloadModel;
import com.vape.sso.model.TokenModel;
import com.vape.sso.model.UserModel;
import com.vape.sso.repository.credential.SessionRepository;
import com.vape.sso.swagger.v1.model.TokenRequest;
import com.vape.sso.swagger.v1.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class TokenService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    /**
     * Activate a new session or update existing one with new jwt token
     * if request is not valid, an empty object is returned
     * @param request TokenRequest
     * @return Token
     */
    public Token activateNewSession(TokenRequest request) {
        return userService.validateTokenRequest(request)
                ? toToken(activate(request))
                : null;
    }

    private TokenModel activate(TokenRequest request) {
        TokenModel existing = sessionRepository.findSessionModelByUser(request.getClientId());
        UserModel user = userService.getUserById(request.getClientId());
        JwtPayloadModel payload = generatePayload(user, request);
        return existing == null
                ? sessionRepository.save(generateNewSession(request, payload))
                : sessionRepository.save(updateExistingSession(existing, payload));
    }

    /**
     * generate jwt payload content
     * @param user UserModel
     * @param TokenRequest TokenRequest
     * @return JwtPayloadModel
     */
    private JwtPayloadModel generatePayload(UserModel user, TokenRequest TokenRequest) {
        return JwtPayloadModel.builder()
                .fullName(user.getFullName())
                .user(user.getClientId())
                .userRoles(user.getUserRole().toString())
                .userAgent(TokenRequest.getUserAgent())
                .build();
    }

    /**
     * Update SessionModel from existing model
     *
     * @param session SessionModel
     * @return SessionModel
     */
    private TokenModel updateExistingSession(TokenModel session, JwtPayloadModel payload) {
        return TokenModel.builder()
                .sessionId(session.getSessionId())
                .userAgent(session.getUserAgent())
                .createdTime(session.getCreatedTime())
                .jwt(jwtService.createJWT(payload, "EXTEND"))
                .user(session.getUser())
                .build();
    }

    /**
     * Create a new SessionModel from TokenRequest
     *
     * @param request TokenRequest
     * @return SessionModel
     */
    private TokenModel generateNewSession(TokenRequest request, JwtPayloadModel payload) {
        return TokenModel.builder()
                .userAgent(request.getUserAgent())
                .createdTime(LocalTime.now())
                .jwt(jwtService.createJWT(payload, "NEW"))
                .user(request.getClientId())
                .build();
    }

    /**
     * mapper method for mapping SessionModel to Token
     *
     * @param session SessionModel
     * @return Token
     */
    private Token toToken(TokenModel session) {
        Token response = new Token();
        response.id(session.getSessionId());
        response.jwt(session.getJwt());
        return response;
    }
}
