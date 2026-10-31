package me.sanyogjadhav.productcatlogservice.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import me.sanyogjadhav.productcatlogservice.model.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JWTService {

    private final SecretKey key;

    public JWTService(@Value("${jwt.secret}") String secret){
        this.key= Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public AuthenticatedUser getAuthenticatedUser(String token){
        Claims claims = extractClaims(token);
        Long userId=Long.valueOf(claims.getSubject());
        String email= claims.get("email",String.class);
        String role = claims.get("role",String.class);
        UserRole userRole =UserRole.valueOf(role);

        return new AuthenticatedUser(
                userId,
                userRole,
                email
        );
    }

}
