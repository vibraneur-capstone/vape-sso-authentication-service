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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class TokenServiceTest {

    @InjectMocks
    private TokenService serviceToTest;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenTransformer tokenTransformer;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should return TokenState with status of ACTIVE")
    void test_get_token_state_active() {
        // Arrange
        String mockJwt = "test";
        String mockJwtId = "test id";
        TokenModel mockTokenModel = TokenModel.builder().jwt(mockJwt).id(mockJwtId).build();
        when(tokenRepository.findTokenModelById(any())).thenReturn(mockTokenModel);
        when(jwtService.isValidJWT(mockJwt)).thenReturn(true);

        // Act
        TokenState actualTokenState = serviceToTest.getTokenState(mockJwtId, mockJwt);

        // Assert
        assertAll("ensure ACTIVE TokenState",
                () -> assertEquals(TokenStatus.ACTIVE, actualTokenState.getStatus())
                );
        verify(tokenRepository, times(1)).findTokenModelById(mockJwtId);
        verify(jwtService, times(1)).isValidJWT(mockJwt);
    }

    @Test
    @DisplayName("should return TokenState with status of INVALID for invalid token")
    void test_get_token_state_invalid_token() {
        // Arrange
        String mockJwt = "test";
        String mockJwtId = "test id";
        TokenModel mockTokenModel = TokenModel.builder().jwt(mockJwt).id(mockJwtId).build();
        when(tokenRepository.findTokenModelById(any())).thenReturn(mockTokenModel);
        when(jwtService.isValidJWT(mockJwt)).thenReturn(false);

        // Act
        TokenState actualTokenState = serviceToTest.getTokenState(mockJwtId, mockJwt);

        // Assert
        assertAll("ensure INVALID TokenState",
                () -> assertEquals(TokenStatus.INVALID, actualTokenState.getStatus())
        );
        verify(tokenRepository, times(1)).findTokenModelById(mockJwtId);
        verify(jwtService, times(1)).isValidJWT(mockJwt);
    }

    @Test
    @DisplayName("should return TokenState with status of INVALID for not found token in mongo")
    void test_get_token_state_token_not_found() {
        // Arrange
        String mockJwt = "test";
        String mockJwtId = "test id";
        when(tokenRepository.findTokenModelById(any())).thenReturn(null);
        when(jwtService.isValidJWT(mockJwt)).thenReturn(true);

        // Act
        TokenState actualTokenState = serviceToTest.getTokenState(mockJwtId, mockJwt);

        // Assert
        assertAll("ensure INVALID TokenState",
                () -> assertEquals(TokenStatus.INVALID, actualTokenState.getStatus())
        );
        verify(tokenRepository, times(1)).findTokenModelById(mockJwtId);
        verify(jwtService, times(0)).isValidJWT(mockJwt);
    }

    @Test
    @DisplayName("should return TokenState with status of INVALID for token no match")
    void test_get_token_state_token_not_matched() {
        // Arrange
        String mockJwt = "test";
        String mockJwtId = "test id";
        TokenModel mockTokenModel = TokenModel.builder().jwt("no match").id(mockJwtId).build();
        when(tokenRepository.findTokenModelById(any())).thenReturn(mockTokenModel);
        when(jwtService.isValidJWT(mockJwt)).thenReturn(true);

        // Act
        TokenState actualTokenState = serviceToTest.getTokenState(mockJwtId, mockJwt);

        // Assert
        assertAll("ensure INVALID TokenState",
                () -> assertEquals(TokenStatus.INVALID, actualTokenState.getStatus())
        );
        verify(tokenRepository, times(1)).findTokenModelById(mockJwtId);
        verify(jwtService, times(0)).isValidJWT(mockJwt);
    }

    @Test
    @DisplayName("should return empty session if user is not validated")
    void test_empty_session() {
        // Arrange
        String mockClientId = "test";
        String mockClientSecret = "test secret";
        TokenRequest mockRequest = new TokenRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);
        when(userService.validateTokenRequest(mockRequest)).thenReturn(false);

        // Act
        Token actual = serviceToTest.activateNewSession(mockRequest);

        // Assert
        assertAll("Ensure the user model return correctly",
                () -> assertNull(actual));
        verify(jwtService, times(0)).createJWT(any(), any());
        verify(tokenRepository, times(0)).findTokenModelByUser(any());
        verify(tokenRepository, times(0)).save(any());
        verify(userService, times(0)).getUserById(any());
        verify(jwtService, times(0)).createJWT(any(), any());
    }

    @Test
    @DisplayName("should activate a new session")
    void test_new_session_activate() {
        // Arrange
        String mockClientId = "test";
        String mockClientSecret = "test secret";
        String mockJWT = "jwt";
        TokenRequest mockRequest = new TokenRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);
        TokenModel mockTokenModel = TokenModel.builder().id("test").jwt("jwt").build();
        Token mockToken = new Token().id("test").jwt("jwt");

        when(userService.validateTokenRequest(mockRequest)).thenReturn(true);
        when(tokenRepository.findTokenModelByUser(mockRequest.getClientId())).thenReturn(null);
        when(tokenRepository.save(any(TokenModel.class))).thenReturn(mockTokenModel);
        when(userService.getUserById(mockRequest.getClientId())).thenReturn(constructMockUser());
        when(jwtService.createJWT(any(JwtPayloadModel.class), eq("NEW"))).thenReturn(mockJWT);
        when(jwtService.generatePayload(any(), any())).thenReturn(JwtPayloadModel.builder().build());
        when(tokenTransformer.toToken(mockTokenModel)).thenReturn(mockToken);

        // Act
        Token actualResponse = serviceToTest.activateNewSession(mockRequest);

        // Assert
        verify(jwtService, times(1)).createJWT(any(JwtPayloadModel.class), eq("NEW"));
        assertAll("Ensure the user model return correctly",
                () -> assertEquals("test", actualResponse.getId()),
                () -> assertEquals("jwt", actualResponse.getJwt()));
    }

    @Test
    @DisplayName("should update an exsiting session")
    void test_new_session_update() {
        // Arrange
        String mockClientId = "test";
        String mockClientSecret = "test secret";
        String mockJWT = "jwt";
        TokenRequest mockRequest = new TokenRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);

        TokenModel mockTokenModel = TokenModel.builder().id("test").jwt("jwt").build();
        Token mockToken = new Token().id("test").jwt("jwt");

        when(userService.validateTokenRequest(mockRequest)).thenReturn(true);
        when(tokenRepository.findTokenModelByUser(mockRequest.getClientId())).thenReturn(mockTokenModel);
        when(tokenRepository.save(any(TokenModel.class))).thenReturn(mockTokenModel);
        when(userService.getUserById(mockRequest.getClientId())).thenReturn(constructMockUser());
        when(jwtService.createJWT(any(JwtPayloadModel.class), eq("EXTEND"))).thenReturn(mockJWT);
        when(jwtService.generatePayload(any(), any())).thenReturn(JwtPayloadModel.builder().build());
        when(tokenTransformer.toToken(mockTokenModel)).thenReturn(mockToken);
        // Act
        Token actualResponse = serviceToTest.activateNewSession(mockRequest);

        // Assert
        verify(jwtService, times(1)).createJWT(any(JwtPayloadModel.class), eq("EXTEND"));
        assertAll("Ensure the user model return correctly",
                () -> assertEquals("test", actualResponse.getId()),
                () -> assertEquals("jwt", actualResponse.getJwt()));
    }

    private UserModel constructMockUser() {
        return UserModel.builder()
                .id("id")
                .clientId("mockUserId")
                .fullName("benxin")
                .hashedPwd("test hash")
                .userRole(new ArrayList<>())
                .build();
    }
}
