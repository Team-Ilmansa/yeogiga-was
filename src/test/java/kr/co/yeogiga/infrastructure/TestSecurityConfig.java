package kr.co.yeogiga.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.yeogiga.common.security.filter.AccessDeniedHandlerImpl;
import kr.co.yeogiga.domain.user.type.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static kr.co.yeogiga.infrastructure.config.security.constant.EndpointConstants.GUEST_ENDPOINTS;
import static kr.co.yeogiga.infrastructure.config.security.constant.EndpointConstants.PUBLIC_ENDPOINTS;
import static kr.co.yeogiga.infrastructure.config.security.constant.EndpointConstants.USER_ENDPOINTS;

@Configuration
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(USER_ENDPOINTS).hasAuthority(Role.USER.name())
                                .requestMatchers(GUEST_ENDPOINTS).hasAuthority(Role.GUEST.name())
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionConfig -> exceptionConfig
                .accessDeniedHandler(new AccessDeniedHandlerImpl(new ObjectMapper()))
                ).build();
    }
}
