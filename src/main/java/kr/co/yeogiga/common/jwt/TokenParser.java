package kr.co.yeogiga.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoder;
import kr.co.yeogiga.common.jwt.base64.CustomBase64UrlDecoder;
import kr.co.yeogiga.infrastructure.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class TokenParser {
    private final JwtParser jwtParser;

    @Autowired
    public TokenParser(JwtProperties jwtProperties) {
        Decoder<InputStream, InputStream> base64UrlDecoder = new CustomBase64UrlDecoder();
        this.jwtParser = Jwts.parser()
                .verifyWith(jwtProperties.getSecretKey())
                .b64Url(base64UrlDecoder)
                .build();
    }

    public Claims parseClaims(String token) {
        return jwtParser.parseSignedClaims(token)
                .getPayload();
    }
}