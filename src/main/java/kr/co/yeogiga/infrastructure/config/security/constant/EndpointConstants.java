package kr.co.yeogiga.infrastructure.config.security.constant;

public final class EndpointConstants {
    private EndpointConstants() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/webjars/**", "/error", "/api-checker/**", "/v1/api/link/checker/**",
            "/api/v1/auth/**"
    };

    public static final String[] USER_ENDPOINTS = {
            "/api/v1/trip/**",
    };
}
