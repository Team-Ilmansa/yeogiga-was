package kr.co.yeogiga.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.yeogiga.application.auth.service.JwtService;
import kr.co.yeogiga.common.jwt.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static kr.co.yeogiga.application.auth.constant.AuthConstants.TOKEN_TYPE;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isValidToken(authToken)) {
            String accessToken = JwtHelper.resolveToken(authToken);
            Authentication authentication = getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 토큰 유효성 검사 메서드
     *
     * @param authToken     사용자가 보낸 토큰
     * @return              토큰 유효 여부
     */
    private boolean isValidToken(String authToken) {
        return authToken != null && authToken.startsWith(TOKEN_TYPE);
    }

    /**
     * 토큰에서 추출한 사용자 정보를 통해 Authentication 구현체를 반환하는 메서드
     *
     * @param token         사용자가 보낸 토큰
     * @return              Authentication 구현체 - UsernamePasswordAuthenticationToken
     */
    private Authentication getAuthentication(String token) {
        Long userId = jwtService.extractUserId(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(userId));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}