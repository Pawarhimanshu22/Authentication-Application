package com.himanshu.auth_backend.controllers;

import com.himanshu.auth_backend.dtos.Request_DTOs.LoginRequestDto;
import com.himanshu.auth_backend.dtos.Request_DTOs.RefreshTokenRequestDto;
import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.TokenResponseDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.entities.RefreshToken;
import com.himanshu.auth_backend.entities.Users;
import com.himanshu.auth_backend.repositories.RefreshTokenRepository;
import com.himanshu.auth_backend.repositories.UserRepository;
import com.himanshu.auth_backend.security.CookieService;
import com.himanshu.auth_backend.security.JwtService;
import com.himanshu.auth_backend.services.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final ModelMapper modelMapper;

    private final RefreshTokenRepository refreshTokenRepository;

    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response )
    {
        // Authenticate the user and generate a JWT token

        Authentication authentication = authenticateUser(loginRequestDto);

        Users user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        String jti = UUID.randomUUID().toString();
        var refreshTokenOb = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(
                        Instant.now().plusMillis(
                                jwtService.getREFRESH_TOKEN_EXPIRATION_TIME()
                        )
                )
                .refreshToken(jwtService.generateRefreshToken(user, jti))
                .revoked(false)
                .build();

        //Refresh Token's Information is saved in the database
        refreshTokenRepository.save(refreshTokenOb);

       //Generate JWT Access Token
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user, refreshTokenOb.getJti());

        //Use Cookie Service to attach the refresh token in the response as a HttpOnly Cookie
        cookieService.attachRefreshTokenCookie(response, refreshToken, jwtService.getREFRESH_TOKEN_EXPIRATION_TIME());
        cookieService.addNoStoreHeaders(response);


        TokenResponseDto tokenResponseDto = TokenResponseDto.of(accessToken, refreshToken, jwtService.getACCESS_TOKEN_EXPIRATION_TIME(), modelMapper.map(user, UserResponseDto.class));

        return ResponseEntity.ok(tokenResponseDto);

    }

    private Authentication authenticateUser(LoginRequestDto loginRequestDto) {

        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequestDto.email(),
                            loginRequestDto.password()
                    ));
            return authenticate;
        }
        catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");

        }
    }


    // Refresh Token And Access Token Renewal Endpoint
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshTokens(
            @RequestBody(required = false) RefreshTokenRequestDto refreshTokenRequestDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        // Step 1: Get the refresh token from cookie / body / header
        String refreshToken = readRefreshTokenFromRequest(refreshTokenRequestDto, request)
                .orElseThrow(() -> new BadCredentialsException("Refresh token is missing"));

        // Step 2: Make sure it is actually a refresh token (not an access token)
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Step 3: Extract token id (jti) and user id from the token
        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);

        // Step 4: Find the matching refresh token record in DB
        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        // Step 5: Reject if it was already revoked
        if (storedRefreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token has been revoked");
        }

        // Step 6: Reject if it has expired
        if (storedRefreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token has expired");
        }

        // Step 7: Reject if this token does not belong to the user in the JWT
        // (NOTE: "!" is required here — token must match the user, else it's invalid)
        if (!storedRefreshToken.getUser().getId().equals(userId)) {
            throw new BadCredentialsException("Refresh token does not belong to the user");
        }

        // Step 8: Revoke the old refresh token (rotation - old one can never be reused)
        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        Users user = storedRefreshToken.getUser();

        // Step 9: Create and save a new refresh token record
        RefreshToken newRefreshTokenRecord = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtService.getREFRESH_TOKEN_EXPIRATION_TIME()))
                .refreshToken(jwtService.generateRefreshToken(user, newJti))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenRecord);

        // Step 10: Generate brand new access token + refresh token (JWT strings)
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user, newRefreshTokenRecord.getJti());

        // Step 11: Send new refresh token back as an HttpOnly cookie
        cookieService.attachRefreshTokenCookie(response, newRefreshToken, jwtService.getREFRESH_TOKEN_EXPIRATION_TIME());
        cookieService.addNoStoreHeaders(response);

        // Step 12: Return new access token (and user info) in response body
        return ResponseEntity.ok(
                TokenResponseDto.of(
                        newAccessToken,
                        newRefreshToken,
                        jwtService.getACCESS_TOKEN_EXPIRATION_TIME(),
                        modelMapper.map(user, UserResponseDto.class)
                )
        );
    }

    // Reads the refresh token from cookie first, then body, then custom header, then Authorization header
    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequestDto refreshTokenRequestDto, HttpServletRequest request) {

        // 1. Prefer reading from the cookie (most secure - HttpOnly)
        if (request.getCookies() != null) {
            Optional<String> fromCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookieService.getRefreshTokenCookieName().equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .filter(value -> !value.isBlank())
                    .findFirst();

            if (fromCookie.isPresent()) {
                return fromCookie;
            }
        }

        // 2. Fallback: request body
        if (refreshTokenRequestDto != null
                && refreshTokenRequestDto.refreshToken() != null
                && !refreshTokenRequestDto.refreshToken().isBlank()) {
            return Optional.of(refreshTokenRequestDto.refreshToken());
        }

        // 3. Fallback: custom header
        String refreshTokenHeader = request.getHeader("X-Refresh-Token");
        if (refreshTokenHeader != null && !refreshTokenHeader.isBlank()) {
            return Optional.of(refreshTokenHeader);
        }

        // 4. Fallback: Authorization: Bearer <token> header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {

            String candidate = authHeader.substring(7).trim();

            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                    // Not a valid refresh token - treat as not found
                }
            }
        }

        return Optional.empty();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto userRequestDto)
    {
        UserResponseDto userResponseDto = authService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        readRefreshTokenFromRequest(null, request).ifPresent( token -> {

            try {

                if (jwtService.isRefreshToken(token)) {

                    String jti = jwtService.getJti(token);

                    refreshTokenRepository.findByJti(jti).ifPresent(
                             refreshToken -> {
                                refreshToken.setRevoked(true);
                                refreshTokenRepository.save(refreshToken);
                            }
                    );
                }

            } catch (JwtException ignored)
            {
            }
        });

        // Use CookieUtil (same behavior)
        cookieService.clearRefreshTokenCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
