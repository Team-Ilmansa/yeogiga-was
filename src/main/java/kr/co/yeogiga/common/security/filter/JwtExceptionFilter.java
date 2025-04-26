package kr.co.yeogiga.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.yeogiga.common.response.error.ErrorResponse;
import kr.co.yeogiga.common.response.error.type.BaseErrorType;
import kr.co.yeogiga.common.response.error.type.CommonErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setJwtExceptionResponse(response, CommonErrorType.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            setJwtExceptionResponse(response, CommonErrorType.INVALID_TOKEN);
        } catch (SignatureException e) {
            setJwtExceptionResponse(response, CommonErrorType.INVALID_TOKEN_SIGNATURE);
        } catch (JwtException e) {
            setJwtExceptionResponse(response, CommonErrorType.UNKNOWN_TOKEN_ERROR);
        }
    }

    private void setJwtExceptionResponse(HttpServletResponse response, BaseErrorType errorType) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(ErrorResponse.from(errorType)));
    }
}
