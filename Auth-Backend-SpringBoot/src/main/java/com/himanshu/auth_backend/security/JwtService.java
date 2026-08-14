package com.himanshu.auth_backend.security;

import com.himanshu.auth_backend.entities.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Data
public class JwtService {

    private final SecretKey SECRET_KEY;
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    private final long REFRESH_TOKEN_EXPIRATION_TIME;
    private final String ISSUER;

    public JwtService(
            @Value("${security.jwt.secret}") String secretKey,
            @Value("${security.jwt.access-ttl-seconds}") long accessTokenExpirationTime,
            @Value("${security.jwt.refresh-ttl-seconds}") long refreshTokenExpirationTime,
            @Value("${security.jwt.issuer}") String issuer) {

        if (secretKey == null || secretKey.length() < 64) {
            throw new IllegalArgumentException(
                    "Invalid Secret Key!! Secret key must be at least 64 characters long"
            );
        }

        this.SECRET_KEY = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );

        this.ACCESS_TOKEN_EXPIRATION_TIME = accessTokenExpirationTime;
        this.REFRESH_TOKEN_EXPIRATION_TIME = refreshTokenExpirationTime;
        this.ISSUER = issuer;
    }

    // Generate Access Token
    public String generateAccessToken(Users user) {

        Instant now = Instant.now();

        List<String> roles = user.getRoles() == null
                ? List.of()
                : user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(ISSUER)
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(
                                now.plusSeconds(ACCESS_TOKEN_EXPIRATION_TIME)
                        )
                )
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Generate Refresh Token
    public String generateRefreshToken(Users user, String jti) {

        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(ISSUER)
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(
                                now.plusSeconds(
                                        REFRESH_TOKEN_EXPIRATION_TIME
                                )
                        )
                )
                .claim("typ", "refresh")
                .signWith(SECRET_KEY)
                .compact();
    }

    // Parse the token
    public Jws<Claims> parse(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

        }
        catch (JwtException e)
        {
            throw e;
        }
    }

    public boolean isAccessToken(String token) {

        Claims claims = parse(token).getPayload();

        return "access".equals(claims.get("typ", String.class));
    }

    public boolean isRefreshToken(String token) {

        Claims claims = parse(token).getPayload();

        return "refresh".equals(claims.get("typ", String.class));
    }

    public UUID getUserId(String token) {

        Claims claims = parse(token).getPayload();

        return UUID.fromString(claims.getSubject());
    }

    public String getJti(String token) {
        return parse(token)
                .getPayload()
                .getId();
    }

    public List<String> getRoles(String token) {
        Claims claims = parse(token).getPayload();

        Object rolesObj = claims.get("roles");

        if (!(rolesObj instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }

    public String getEmail(String token) {
        Claims claims = parse(token).getPayload();
        return claims.get("email", String.class);
    }



}