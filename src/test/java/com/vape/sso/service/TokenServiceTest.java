package com.vape.sso.service;

import com.vape.sso.model.JwtPayloadModel;
import com.vape.sso.model.TokenModel;
import com.vape.sso.model.UserModel;
import com.vape.sso.repository.credential.SessionRepository;
import com.vape.sso.swagger.v1.model.SessionRequest;
import com.vape.sso.swagger.v1.model.Token;
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
    private SessionRepository sessionRepository;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should return empty session if user is not validated")
    void test_empty_session() {
        // Arrange
        String mockClientId = "test";
        String mockClientSecret = "test secret";
        SessionRequest mockRequest = new SessionRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);
        when(userService.validateSessionRequest(mockRequest)).thenReturn(false);

        // Act
        Token actual = serviceToTest.activateNewSession(mockRequest);

        // Assert
        assertAll("Ensure the user model return correctly",
                () -> assertNull(actual.getSecrete()),
                () -> assertNull(actual.getSessionId()));
        verify(jwtService, times(0)).createJWT(any(), any());
        verify(sessionRepository, times(0)).findSessionModelByUser(any());
        verify(sessionRepository, times(0)).save(any());
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
        SessionRequest mockRequest = new SessionRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);

        when(userService.validateSessionRequest(mockRequest)).thenReturn(true);
        when(sessionRepository.findSessionModelByUser(mockRequest.getClientId())).thenReturn(null);
        when(sessionRepository.save(any(TokenModel.class))).thenReturn(TokenModel.builder().sessionId("test").jwt("jwt").build());
        when(userService.getUserById(mockRequest.getClientId())).thenReturn(constructMockUser());
        when(jwtService.createJWT(any(JwtPayloadModel.class), eq("NEW"))).thenReturn(mockJWT);
        // Act
        Token actualResponse = serviceToTest.activateNewSession(mockRequest);

        // Assert
        verify(jwtService, times(1)).createJWT(any(JwtPayloadModel.class), eq("NEW"));
        assertAll("Ensure the user model return correctly",
                () -> assertEquals("test", actualResponse.getSessionId()),
                () -> assertEquals("jwt", actualResponse.getSecrete()));
    }

    @Test
    @DisplayName("should update an exsiting session")
    void test_new_session_update() {
        // Arrange
        String mockClientId = "test";
        String mockClientSecret = "test secret";
        String mockJWT = "jwt";
        SessionRequest mockRequest = new SessionRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);

        TokenModel mockSession = TokenModel.builder().sessionId("test").jwt("jwt").build();

        when(userService.validateSessionRequest(mockRequest)).thenReturn(true);
        when(sessionRepository.findSessionModelByUser(mockRequest.getClientId())).thenReturn(mockSession);
        when(sessionRepository.save(any(TokenModel.class))).thenReturn(mockSession);
        when(userService.getUserById(mockRequest.getClientId())).thenReturn(constructMockUser());
        when(jwtService.createJWT(any(JwtPayloadModel.class), eq("EXTEND"))).thenReturn(mockJWT);
        // Act
        Token actualResponse = serviceToTest.activateNewSession(mockRequest);

        // Assert
        verify(jwtService, times(1)).createJWT(any(JwtPayloadModel.class), eq("EXTEND"));
        assertAll("Ensure the user model return correctly",
                () -> assertEquals("test", actualResponse.getSessionId()),
                () -> assertEquals("jwt", actualResponse.getSecrete()));
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
