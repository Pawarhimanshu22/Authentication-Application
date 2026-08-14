package com.himanshu.auth_backend.security;

import com.himanshu.auth_backend.entities.Provider;
import com.himanshu.auth_backend.entities.RefreshToken;
import com.himanshu.auth_backend.entities.Users;
import com.himanshu.auth_backend.repositories.RefreshTokenRepository;
import com.himanshu.auth_backend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        logger.info("Authentication Success");
        logger.info("Authentication Name: " + authentication.getName());
        logger.info("Authentication Principal: " + authentication.getPrincipal());

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Identify the user based on the OAuth2User information
        String registrationId = "Unknown";
        if (authentication instanceof OAuth2AuthenticationToken token) {
            registrationId = token.getAuthorizedClientRegistrationId();
        }

        logger.info("Registration Id: " + registrationId);
        logger.info("User Name: " + oAuth2User.getName());
        logger.info("User Attributes: " + oAuth2User.getAttributes().toString());

        // Step 1: Find existing user or create + save a new one based on provider
        Users user = switch (registrationId) {

            case "google" -> {
                String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
                String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                String picture = oAuth2User.getAttributes().getOrDefault("picture", "").toString();

                yield userRepository.findByEmail(email).orElseGet(() -> {
                    Users newUser = Users.builder()
                            .email(email)
                            .name(name)
                            .image(picture)
                            .enable(true)
                            .provider(Provider.GOOGLE)
                            .build();

                    Users savedUser = userRepository.save(newUser);
                    logger.info("New Google user saved: " + savedUser.getEmail());
                    return savedUser;
                });
            }

            case "github" -> {
                String email = oAuth2User.getAttributes().getOrDefault("email", "").toString();
                String name = oAuth2User.getAttributes().getOrDefault("name", "").toString();
                String picture = oAuth2User.getAttributes().getOrDefault("avatar_url", "").toString();

                // GitHub may return null for "name" or "email", depending on user's profile/privacy settings.

                yield userRepository.findByEmail(email).orElseGet(() -> {
                    Users newUser = Users.builder()
                            .email(email)
                            .name(name)
                            .image(picture)
                            .enable(true)
                            .provider(Provider.GITHUB)
                            .build();

                    Users savedUser = userRepository.save(newUser);
                    logger.info("New GitHub user saved: " + savedUser.getEmail());
                    return savedUser;
                });
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported OAuth2 provider: " + registrationId
            );
        };

        logger.info("User is there in database or just created: " + user.getEmail());

        // Step 2: Create and save a refresh token for this user
        String jti = UUID.randomUUID().toString();

        RefreshToken refreshTokenObj = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .revoked(false)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtService.getREFRESH_TOKEN_EXPIRATION_TIME()))
                .build();

        refreshTokenRepository.save(refreshTokenObj);

        // Step 3: Generate access token + refresh token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenObj.getJti());

        // Step 4: Send refresh token as HttpOnly cookie
        cookieService.attachRefreshTokenCookie(
                response,
                refreshToken,
                (int) jwtService.getREFRESH_TOKEN_EXPIRATION_TIME()
        );

        // Step 5: Send access token back in response
        response.setContentType("text/plain");
        response.getWriter().write(accessToken);
    }
}