package com.himanshu.auth_backend.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CookieService {

    private final String refreshTokenCookieName;
    private final boolean cookieHttpOnly;
    private final boolean cookieSecure;
    private final long cookieMaxAge;
    private final String cookieDomain;
    private final String cookiePath;
    private final String cookieSameSite;

    private final Logger logger = LoggerFactory.getLogger(CookieService.class);

    public CookieService(
            @Value("${security.jwt.refresh-token-cookie-name}")
            String refreshTokenCookieName,

            @Value("${security.jwt.cookie-http-only}")
            boolean cookieHttpOnly,

            @Value("${security.jwt.cookie-secure}")
            boolean cookieSecure,

            @Value("${security.jwt.cookie-max-age}")
            long cookieMaxAge,

            @Value("${security.jwt.cookie-domain:}")
            String cookieDomain,

            @Value("${security.jwt.cookie-path:/}")
            String cookiePath,

            @Value("${security.jwt.cookie-same-site:Lax}")
            String cookieSameSite
    ) {
        this.refreshTokenCookieName = refreshTokenCookieName;
        this.cookieHttpOnly = cookieHttpOnly;
        this.cookieSecure = cookieSecure;
        this.cookieMaxAge = cookieMaxAge;
        this.cookieDomain = cookieDomain;
        this.cookiePath = cookiePath;
        this.cookieSameSite = cookieSameSite;
    }

    // Create method to attach cookie to response
    public void attachRefreshTokenCookie(
            HttpServletResponse response,
            String value,
            long maxAge
    ) {

        logger.info("Attaching refresh token cookie");

        var responseCookieBuilder = ResponseCookie
                .from(refreshTokenCookieName, value)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(maxAge)
                .path(cookiePath)
                .sameSite(cookieSameSite);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            responseCookieBuilder.domain(cookieDomain);
        }

        ResponseCookie responseCookie = responseCookieBuilder.build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                responseCookie.toString()
        );
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {

        ResponseCookie responseCookie = ResponseCookie
                .from(refreshTokenCookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(0)
                .path(cookiePath)
                .sameSite(cookieSameSite)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                responseCookie.toString()
        );
    }

    //Anti caching header
    public void addNoStoreHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, proxy-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Surrogate-Control", "no-store");
    }
}