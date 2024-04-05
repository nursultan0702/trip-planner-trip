package com.tripplannertrip.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class JwtUtil {

    private String secretKey;

    public Claims extractAllClaims(String token) {
        byte[] secretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(secretBytes, Jwts.SIG.HS256.getId());
        return Jwts.parser().
                verifyWith(key)
                .build()
                .parseSignedClaims(token).getPayload();
    }

    // Example method to extract a specific claim
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
}
