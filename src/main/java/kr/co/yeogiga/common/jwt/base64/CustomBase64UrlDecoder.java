package kr.co.yeogiga.common.jwt.base64;

import io.jsonwebtoken.io.Decoder;
import io.jsonwebtoken.io.DecodingException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class CustomBase64UrlDecoder implements Decoder<InputStream, InputStream> {
    @Override
    public InputStream decode(InputStream inputStream) throws DecodingException {
        try {
            byte[] bytes = inputStream.readAllBytes();
            byte[] decodedBytes = Base64.getUrlDecoder().decode(bytes);
            return new ByteArrayInputStream(decodedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Decoding failed", e);
        }
    }
}