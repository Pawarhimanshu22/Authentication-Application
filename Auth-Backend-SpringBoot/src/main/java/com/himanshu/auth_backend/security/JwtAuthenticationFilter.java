package com.himanshu.auth_backend.security;

import com.himanshu.auth_backend.entities.Users;
import com.himanshu.auth_backend.helpers.UserHelper;
import com.himanshu.auth_backend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */

    private final JwtService jwtService;
    private final UserRepository  userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader("Authorization");

        // No Authorization header → continue normally
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        // Empty Bearer token
        if (token.isEmpty()) {
            sendUnauthorized(response, "Bearer token is missing");
            return;
        }

        try {

            // Parse + verify signature + validate expiration
            Jws<Claims> parsedToken = jwtService.parse(token);

            Claims payload = parsedToken.getPayload();

            String userID = payload.getSubject();

            UUID userUuid = UserHelper.parseUUID(userID);

            //User mil gaya from DB
            userRepository.findById(userUuid)
                    .ifPresent(Users::getAuthorities)

            // Only Access Tokens are accepted by this filter
            String tokenType = payload.get("typ", String.class);

            if (!"access".equals(tokenType)) {
                sendUnauthorized(response, "Invalid token type");
                return;
            }

            // Get user ID from JWT subject
            String userId = payload.getSubject();

            if (userId == null || userId.isBlank()) {
                sendUnauthorized(response, "User ID is missing from token");
                return;
            }

            // Get roles from JWT
            List<SimpleGrantedAuthority> authorities =
                    getAuthorities(payload);

            // Create Spring Security Authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            authorities
                    );

            // Store authentication in SecurityContext
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            // Continue request
            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {

            // Invalid / expired / malformed / tampered token
            SecurityContextHolder.clearContext();

            sendUnauthorized(
                    response,
                    "Invalid or expired access token"
            );
        }
    }

    private List<SimpleGrantedAuthority> getAuthorities(
            Claims claims) {

        Object rolesObject = claims.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return List.of();
        }

        return roles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write(
                """
                {
                    "status": 401,
                    "error": "Unauthorized",
                    "message": "%s"
                }
                """.formatted(message)
        );
    }
}