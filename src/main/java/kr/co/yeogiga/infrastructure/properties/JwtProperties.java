package kr.co.yeogiga.infrastructure.properties;

import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
@Component
public class JwtProperties {
    @Value("${jwt.secret-key}")
    String SECRET_KEY;
    @Value("${jwt.issuer}")
    String issuer;
    @Value("${jwt.expiration.access-token}")
    long accessTokenExpiration;
    @Value("${jwt.expiration.refresh-token}")
    long refreshTokenExpiration;

    public SecretKey getSecretKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
}