package com.vape.sso.mapper;

import com.vape.sso.model.TokenModel;
import com.vape.sso.swagger.v1.model.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class TokenTransformerTest {

    @InjectMocks
    private TokenTransformer mapperToTest;

    @BeforeEach()
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("should map to Token object")
    void test_mapping_success() {
        // Arrange
        TokenModel tokenModel = TokenModel.builder().id("test").jwt("test jwt").user("user").build();

        // Act
        Token actualMapped = mapperToTest.toToken(tokenModel);

        // Assert
        assertAll("ensure proper mapping",
                () -> assertNotNull(actualMapped),
                () -> assertEquals("test", actualMapped.getId()),
                () -> assertEquals("test jwt", actualMapped.getJwt())
                );
    }
}
