package com.tta.moneta.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiTokenIntrospectorTest {

    private ApiTokenIntrospector apiTokenIntrospector;

    @BeforeEach
    void setUp() {
        apiTokenIntrospector = new ApiTokenIntrospector();
        ReflectionTestUtils.setField(apiTokenIntrospector, "apiToken", "token-test");

    }

    @ParameterizedTest
    @ValueSource(strings = {"", "token", "test-token", "test"})
    void introspect__shouldThrowAuthenticationExceptionWhenTokenIsNotEqualToApiToken(String invalidToken) {
        assertThatThrownBy(() -> apiTokenIntrospector.introspect(invalidToken))
                .isInstanceOf(ApiTokenAuthenticationException.class)
                .hasMessage("Invalid token");
    }

    @Test
    void introspect__shouldReturnOAuth2AuthenticatedPrincipal() {
        OAuth2AuthenticatedPrincipal introspect = apiTokenIntrospector.introspect("token-test");
        assertThat(introspect).isNotNull();
        assertThat(introspect).isInstanceOf(OAuth2AuthenticatedPrincipal.class);
    }
}