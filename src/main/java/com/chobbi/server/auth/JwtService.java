package com.chobbi.server.auth;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.enums.RoleEnums;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(AccountEntity account) {
        List<String> roles = account.getAccountRoles().stream()
                .map(ar -> ar.getRolesEntity().getRole().name())
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(account.getId().toString())
                .claim("email", account.getEmail())
                .claim("name", account.getName() != null ? account.getName() : "")
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getAccountIdFromToken(String token) {
        String subject = validateAndGetClaims(token).getSubject();
        return subject != null ? Long.parseLong(subject) : null;
    }

    /** Lấy email từ claim (sau khi đã verify token). */
    public String getEmailFromToken(String token) {
        Claims claims = validateAndGetClaims(token);
        return claims.get("email", String.class);
    }
}
