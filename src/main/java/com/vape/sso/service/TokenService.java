package com.vape.sso.service;

import com.vape.sso.mapper.TokenTransformer;
import com.vape.sso.model.JwtPayloadModel;
import com.vape.sso.model.TokenModel;
import com.vape.sso.model.UserModel;
import com.vape.sso.repository.credential.TokenRepository;
import com.vape.sso.swagger.v1.model.TokenRequest;
import com.vape.sso.swagger.v1.model.Token;
import com.vape.sso.swagger.v1.model.TokenState;
import com.vape.sso.swagger.v1.model.TokenStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenTransformer tokenTransformer;

    public TokenState getTokenState(String tokenId, String token) {
        TokenModel tokenModel = tokenRepository.findTokenModelById(tokenId);
        TokenState tokenState = new TokenState().status(TokenStatus.INVALID);
        if (tokenModel != null && token.equals(tokenModel.getJwt()) && jwtService.isValidJWT(token)) {
            tokenState.setStatus(TokenStatus.ACTIVE);
        }
        return tokenState;
    }

    /**
     * Activate a new session or update existing one with new jwt token
     * if request is not valid, an empty object is returned
     * @param request TokenRequest
     * @return Token
     */
    public Token activateNewSession(TokenRequest request) {
        return userService.validateTokenRequest(request)
                ? tokenTransformer.toToken(activate(request))
                : null;
    }

    private TokenModel activate(TokenRequest request) {
        TokenModel existing = tokenRepository.findTokenModelByUser(request.getClientId());
        UserModel user = userService.getUserById(request.getClientId());
        JwtPayloadModel payload = jwtService.generatePayload(user, request);
        return existing == null
                ? tokenRepository.save(generateNewSession(request, payload))
                : tokenRepository.save(updateExistingSession(existing, payload));
    }

    /**
     * Update SessionModel from existing model
     *
     * @param session SessionModel
     * @return SessionModel
     */
    private TokenModel updateExistingSession(TokenModel session, JwtPayloadModel payload) {
        return TokenModel.builder()
                .id(session.getId())
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
}
