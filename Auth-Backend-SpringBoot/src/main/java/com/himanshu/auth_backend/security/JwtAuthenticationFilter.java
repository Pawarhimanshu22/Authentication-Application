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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        logger.info("Authorization header: " + header);

        // Agar Bearer token nahi hai to seedha aage badh jao
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);


        try {

            //Check for access token
            if (!jwtService.isAccessToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            logger.info("Access token: " + token);

            // Step 1: token verify karo aur claims nikalo
            Jws<Claims> parse = jwtService.parse(token);
            Claims payload = parse.getPayload();

            String userId = payload.getSubject();
            UUID userUuid = UserHelper.parseUUID(userId);

            // Step 2: DB se user dhundo
            userRepository.findById(userUuid).ifPresent(user -> {

                if (user.isEnabled())
                {

                // Step 3: roles ko GrantedAuthority me convert karo
                List<GrantedAuthority> authorities = getAuthorities(user);

                // Step 4: authentication object banao
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                if (SecurityContextHolder.getContext().getAuthentication() == null)
                    // Step 5: security context me set karo
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            });

        }
        catch (JwtException | IllegalArgumentException e) {
            // invalid / expired / tampered token -> user simply unauthenticated rahega
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> getAuthorities(Users user) {
        if (user.getRoles() == null) {
            return List.of();
        }
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());
    }
}