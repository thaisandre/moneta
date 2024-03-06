package com.tta.moneta.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;

@Component
public class ApiTokenIntrospector implements OpaqueTokenIntrospector {

    @Value("${api.token}")
    private String apiToken;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        if(!apiToken.equals(token)) throw new ApiTokenAuthenticationException("Invalid token");
        return new OAuth2IntrospectionAuthenticatedPrincipal(getAttributes(token), NO_AUTHORITIES);
    }

    private Map<String, Object> getAttributes(String token) {
        return Map.of("active", token.equals(apiToken));
    }
}
