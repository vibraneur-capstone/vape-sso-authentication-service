package com.vape.sso.service;


import com.vape.sso.model.UserModel;
import com.vape.sso.repository.credential.UserRepository;
import com.vape.sso.swagger.v1.model.SessionRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @InjectMocks
    private UserService serviceToTest;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SaltService credentialSaltService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should get user by client id")
    void test_get_user_by_id_success() {
        // Arrange
        String mockUserId = "test id";
        UserModel expectedUserModel = UserModel.builder()
                .clientId(mockUserId)
                .fullName("benxin")
                .hashedPwd("test hash")
                .build();

        when(userRepository.findUsersByClientId(mockUserId)).thenReturn(expectedUserModel);

        // Act
        UserModel actualUserModel = serviceToTest.getUserById(mockUserId);

        // Assert
        assertAll("Ensure the user model return correctly",
                () -> assertEquals(expectedUserModel.getClientId(), actualUserModel.getClientId()),
                () -> assertEquals(expectedUserModel.getFullName(), actualUserModel.getFullName()),
                () -> assertEquals(expectedUserModel.getHashedPwd(), actualUserModel.getHashedPwd()));
    }

    @Test
    @DisplayName("should return true for valid user")
    void test_validate_user_true() {
        String mockClientId = "test";
        String mockId = "id";
        String mockSalt = "mock salt";
        String mockClientSecret = "test secret";

        String hashPwd = DigestUtils.sha256Hex(String.format("%s%s", mockSalt, mockClientSecret));
        String associateId = DigestUtils.sha256Hex(String.format("%s%s", mockId, mockClientId));

        UserModel expectedUserModel = UserModel.builder()
                .id(mockId)
                .clientId(mockClientId)
                .fullName("benxin")
                .hashedPwd(hashPwd)
                .build();

        SessionRequest mockRequest = new SessionRequest()
                .clientId(mockClientId)
                .clientSecret(mockClientSecret);

        when(userRepository.findUsersByClientId(mockClientId)).thenReturn(expectedUserModel);
        when(credentialSaltService.getSaltByAssociateId(associateId)).thenReturn(mockSalt);

        // Act
        boolean actual = serviceToTest.validateSessionRequest(mockRequest);

        // Assert
        assertAll("Ensure true is returned",
                () -> assertTrue(actual));
    }

    @Test
    @DisplayName("should return false for user not found")
    void test_validate_user_false_for_user_not_found() {
        // Arrange
        SessionRequest mockRequest = new SessionRequest()
                .clientId("mockClientId")
                .clientSecret("mockClientSecret");

        when(userRepository.findUsersByClientId("mockClientId")).thenReturn(null);

        // Act & Assert

        assertAll("Ensure false is returned",
                () -> assertFalse(serviceToTest.validateSessionRequest(mockRequest)));
    }

}
