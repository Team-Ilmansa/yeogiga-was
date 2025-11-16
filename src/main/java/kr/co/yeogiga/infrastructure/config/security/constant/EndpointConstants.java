package kr.co.yeogiga.infrastructure.config.security.constant;

public final class EndpointConstants {
    private EndpointConstants() {}

    public static final String[] PUBLIC_ENDPOINTS = {
            "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**",
            "/webjars/**", "/error", "/api-checker/**", "/v1/api/link/checker/**",
            "/api/v1/auth/**", "/api/v1/oauth/sign-in/**", "/api/v1/weather", "/api/v1/uprising-places"
    };

    public static final String[] USER_ENDPOINTS = {
            "/api/v1/trip/**",
            "/api/v1/users/**",
            "/api/v1/places/**",
            "/api/v1/settlement/**"
    };

    public static final String[] GUEST_ENDPOINTS = {
            "/api/v1/oauth/register"
    };
}
