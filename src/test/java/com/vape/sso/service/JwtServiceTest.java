package com.vape.sso.service;

import com.vape.sso.config.JwtConfig;
import com.vape.sso.model.JwtPayloadModel;
import com.vape.sso.model.UserModel;
import com.vape.sso.swagger.v1.model.TokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@EnableAutoConfiguration
public class JwtServiceTest {

    @InjectMocks
    private JwtService serviceToTest;

    @Mock
    private JwtConfig jwtConfig;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        long maxAge = 10000;
        when(jwtConfig.getSignatureAlgorithm()).thenReturn("HS256");
        when(jwtConfig.getSecretKey()).thenReturn("CC4BBA2415B838D58193ADE9E492B");
        when(jwtConfig.getMaxAge()).thenReturn(maxAge);
    }

    @Test
    @DisplayName("should return true for valid jwt token")
    void test_isValidJWT_true() {
        // Arrange
        String mockJWT = getValidMockJWT();

        // Act
        boolean actual = serviceToTest.isValidJWT(mockJWT);

        // Assert
        assertAll("ensure true",
                () -> assertTrue(actual));
    }

    @Test
    @DisplayName("should return false for invalid jwt token")
    void test_isValidJWT_false() {
        // Arrange
        String mockJWT = getInValidMockJWT();

        // Act
        boolean actual = serviceToTest.isValidJWT(mockJWT);

        // Assert
        assertAll("ensure false",
                () -> assertFalse(actual));
    }

    @Test
    @DisplayName("should return false for expired jwt token")
    void test_isValidJWT_expired() {
        // Arrange
        long maxAge = 0;
        when(jwtConfig.getMaxAge()).thenReturn(maxAge);
        String mockJWT = getInValidMockJWT();

        // Act
        boolean actual = serviceToTest.isValidJWT(mockJWT);

        // Assert
        assertAll("ensure false",
                () -> assertFalse(actual));
    }

    @Test
    @DisplayName("should construct JwtPayloadModel")
    void test_generate_JwtPayloadModel() {
        // Arrange
        //UserModel user, TokenRequest tokenRequest
        UserModel user = UserModel.builder()
                .clientId("client id")
                .fullName("full name")
                .hashedPwd("pwd")
                .id("id")
                .userRole(new ArrayList<>())
                .build();
        TokenRequest request = new TokenRequest().userAgent("test");

        JwtPayloadModel expected = JwtPayloadModel.builder()
                .fullName("full name")
                .user(user.getClientId())
                .userAgent(request.getUserAgent())
                .userRoles(user.getUserRole().toString())
                .build();

        // Act
        JwtPayloadModel actual = serviceToTest.generatePayload(user, request);

        // Assert
        assertAll("ensure JwtPayloadModel generation",
                () -> assertEquals(expected.getFullName(), actual.getFullName()),
                () -> assertEquals(expected.getUser(), actual.getUser()),
                () -> assertEquals(expected.getUserAgent(), actual.getUserAgent()),
                () -> assertEquals(expected.getUserRoles(), actual.getUserRoles())
                );
    }

    @Test
    @DisplayName("should create jwt token")
    void test_jwt_creation() {
        // Arrange
        when(jwtConfig.getIssuer()).thenReturn("vape-sso");
        JwtPayloadModel jwtPayloadModel = JwtPayloadModel.builder().fullName("").user("").userRoles("").userAgent("").build();

        // Act
        String jwt = serviceToTest.createJWT(jwtPayloadModel, "NEW");

        // Assert
        assertAll("ensure",
                () -> assertNotNull(jwt)
                );
    }

    private String getValidMockJWT(){
        JwtPayloadModel mockJwtPayloadModel = JwtPayloadModel.builder()
                .fullName("full name")
                .user("")
                .userRoles("")
                .userAgent("")
                .build();
        return serviceToTest.createJWT(mockJwtPayloadModel, "NEW");
    }

    private String getInValidMockJWT(){
        String mockJwt = getValidMockJWT();
        // temper with a valid jwt
        mockJwt = mockJwt + "b";
        return mockJwt;
    }
}
