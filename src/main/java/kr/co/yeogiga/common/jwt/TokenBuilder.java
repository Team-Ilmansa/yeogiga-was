package kr.co.yeogiga.common.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoder;
import kr.co.yeogiga.common.jwt.base64.CustomBase64UrlEncoder;
import kr.co.yeogiga.infrastructure.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Date;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class TokenBuilder {
    private final Encoder<OutputStream, OutputStream> base64UrlEncoder = new CustomBase64UrlEncoder();
    private final JwtProperties jwtProperties;

    public String build(String subject, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .b64Url(base64UrlEncoder)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }
}