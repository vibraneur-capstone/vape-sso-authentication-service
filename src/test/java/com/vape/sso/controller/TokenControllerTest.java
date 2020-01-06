package com.vape.sso.controller;

import com.vape.sso.service.TokenService;
import com.vape.sso.swagger.v1.model.Token;
import com.vape.sso.swagger.v1.model.TokenRequest;
import com.vape.sso.swagger.v1.model.TokenState;
import com.vape.sso.swagger.v1.model.TokenStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TokenControllerTest {
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TokenController controllerToTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should return 200 and valid status for token")
    void test_token_validation_success() {
        // Arrange
        String mockTokenId = "test id";
        String mockJwtToken = "jwt token";
        when(tokenService.getTokenState(eq(mockTokenId), eq(mockJwtToken))).thenReturn(new TokenState().status(TokenStatus.ACTIVE));

        // Act
        ResponseEntity<TokenState> actualResponse = controllerToTest.getSession(mockTokenId, mockJwtToken);

        // Assert
        assertAll("ensure OK",
                () -> assertNotNull(actualResponse.getBody()),
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()),
                () -> assertEquals(TokenStatus.ACTIVE, actualResponse.getBody().getStatus()));
        verify(tokenService, times(1)).getTokenState(mockTokenId, mockJwtToken);
    }

    @Test
    @DisplayName("should return 401 if token is invalid")
    void test_token_validation_invalid() {
        // Arrange
        String mockTokenId = "test id";
        String mockJwtToken = "jwt token";
        when(tokenService.getTokenState(eq(mockTokenId), eq(mockJwtToken))).thenReturn(new TokenState().status(TokenStatus.INVALID));

        // Act
        ResponseEntity<TokenState> actualResponse = controllerToTest.getSession(mockTokenId, mockJwtToken);

        // Assert
        assertAll("ensure UNAUTHORIZED",
                () -> assertNotNull(actualResponse.getBody()),
                () -> assertEquals(HttpStatus.UNAUTHORIZED, actualResponse.getStatusCode()),
                () -> assertEquals(TokenStatus.INVALID, actualResponse.getBody().getStatus()));
        verify(tokenService, times(1)).getTokenState(mockTokenId, mockJwtToken);
    }

    @Test
    @DisplayName("should return 400 if token id is null or empty")
    void test_token_validation_id_empty_or_null() {
        // Arrange
        String mockEmptyTokenId = "";
        String mockJwtToken = "jwt token";

        // Act
        ResponseEntity<TokenState> actualResponseEmptyId = controllerToTest.getSession(mockEmptyTokenId, mockJwtToken);
        ResponseEntity<TokenState> actualResponseNullId = controllerToTest.getSession(null, mockJwtToken);

        // Assert
        assertAll("ensure BAD REQUEST",
                () -> assertNull(actualResponseEmptyId.getBody().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponseEmptyId.getStatusCode()),
                () -> assertNull(actualResponseNullId.getBody().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponseNullId.getStatusCode())
        );
        verify(tokenService, times(0)).getTokenState(any(), any());
    }

    @Test
    @DisplayName("should return 400 if token is null or empty")
    void test_token_validation_token_empty_or_null() {
        // Arrange
        String mockJwtToken = "";
        String mockTokenId = "jwt token id";

        // Act
        ResponseEntity<TokenState> actualResponseEmptyToken = controllerToTest.getSession(mockTokenId, mockJwtToken);
        ResponseEntity<TokenState> actualResponseNullToken = controllerToTest.getSession(mockTokenId, null);

        // Assert
        assertAll("ensure BAD REQUEST",
                () -> assertNull(actualResponseEmptyToken.getBody().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponseEmptyToken.getStatusCode()),
                () -> assertNull(actualResponseNullToken.getBody().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponseNullToken.getStatusCode())
        );
        verify(tokenService, times(0)).getTokenState(any(), any());
    }

    @Test
    @DisplayName("should return proper token")
    void test_token_success() {
        // Arrange
        String mockClientId = "test";
        TokenRequest mockRequest = new TokenRequest().clientId(mockClientId).clientSecret("test");
        Token mockToken = new Token().jwt("test token value").id("test id");
        when(tokenService.activateNewSession(mockRequest)).thenReturn(mockToken);

        // Act
        ResponseEntity<Token> actualResponse = controllerToTest.postNewSession(mockClientId, mockRequest);

        // Assert
        assertAll("ensure token is returned",
                () -> assertNotNull(actualResponse.getBody()),
                () -> assertEquals(mockToken.getId(), actualResponse.getBody().getId()),
                () -> assertEquals(mockToken.getJwt(), actualResponse.getBody().getJwt()),
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()));

        verify(tokenService, times(1)).activateNewSession(mockRequest);
    }

    @Test
    @DisplayName("should give 400 if request is invalid")
    void test_unauthorized() {
        // Arrange
        String mockClientId = "test";
        TokenRequest mockRequest = new TokenRequest().clientId(mockClientId).clientSecret("test");
        when(tokenService.activateNewSession(mockRequest)).thenReturn(null);

        // Act
        ResponseEntity<Token> actualResponse = controllerToTest.postNewSession(mockClientId, mockRequest);

        // Assert
        assertAll("ensure 400 is returned",
                () -> assertNull(actualResponse.getBody().getId()),
                () -> assertNull(actualResponse.getBody().getJwt()),
                () -> assertEquals(HttpStatus.UNAUTHORIZED, actualResponse.getStatusCode())
        );
        verify(tokenService, times(1)).activateNewSession(mockRequest);
    }

    @Test
    @DisplayName("should give bad request if client id is empty")
    void test_bad_request_client_id_empty() {
        // Arrange
        String mockClientId = "";
        TokenRequest mockRequest = new TokenRequest().clientId(mockClientId).clientSecret("test");

        // Act
        ResponseEntity<Token> actualResponse = controllerToTest.postNewSession(mockClientId, mockRequest);

        // Assert
        assertAll("ensure null token and bad request",
                () -> assertNull(actualResponse.getBody().getJwt()),
                () -> assertNull(actualResponse.getBody().getId()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode())
        );
        verify(tokenService, times(0)).activateNewSession(mockRequest);
    }

    @Test
    @DisplayName("should give bad request if client id is null")
    void test_bad_request_client_id_null() {
        // Arrange
        TokenRequest mockRequest = new TokenRequest().clientId("test").clientSecret("test");

        // Act
        ResponseEntity<Token> actualResponse = controllerToTest.postNewSession(null, mockRequest);

        // Assert
        assertAll("ensure null token and bad request",
                () -> assertNull(actualResponse.getBody().getJwt()),
                () -> assertNull(actualResponse.getBody().getId()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode())
        );
        verify(tokenService, times(0)).activateNewSession(mockRequest);
    }

    @Test
    @DisplayName("should give bad request if client id do not match with request body")
    void test_bad_request_client_id_no_match_with_request() {
        // Arrange
        String mockClientId = "test";
        String mockNoMatchedClientId = "no match";
        TokenRequest mockRequest = new TokenRequest().clientId(mockClientId).clientSecret("test");

        // Act
        ResponseEntity<Token> actualResponse = controllerToTest.postNewSession(mockNoMatchedClientId, mockRequest);

        // Assert
        assertAll("ensure null token and bad request",
                () -> assertNull(actualResponse.getBody().getJwt()),
                () -> assertNull(actualResponse.getBody().getId()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode())
        );
        verify(tokenService, times(0)).activateNewSession(mockRequest);
    }
}
