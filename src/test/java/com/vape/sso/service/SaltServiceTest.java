package com.vape.sso.service;

import com.vape.sso.model.CredentialSaltModel;
import com.vape.sso.repository.credentialSalt.CredentialSaltRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class SaltServiceTest {

    @InjectMocks
    private SaltService serviceToTest;

    @Mock
    private CredentialSaltRepository credentialSaltRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should get salt from associatedId")
    void test_get_user_salt() {
        // Arrange
        String mockAssociatedId = "test id";
        String mockSalt = "salt";
        CredentialSaltModel credentialSaltModel = CredentialSaltModel
                .builder()
                .associateId(mockAssociatedId)
                .salt(mockSalt)
                .id("object id")
                .build();
        when(credentialSaltRepository.findCredentialSaltsByAssociateId(mockAssociatedId)).thenReturn(credentialSaltModel);
        // Act
        String actualSalt = serviceToTest.getSaltByAssociateId(mockAssociatedId);

        // Assert
        assertAll("ensure correct salt",
                () -> assertEquals(mockSalt, actualSalt)
                );
        verify(credentialSaltRepository, times(1)).findCredentialSaltsByAssociateId(mockAssociatedId);
    }
}
