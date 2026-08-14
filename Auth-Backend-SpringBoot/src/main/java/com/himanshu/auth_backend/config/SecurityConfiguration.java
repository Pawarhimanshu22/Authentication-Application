package com.himanshu.auth_backend.config;

import com.himanshu.auth_backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;


import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;


/**
     * This method creates an in-memory user details service with three users.
     * Each user has a username, password, and assigned roles.
     *
     * @return an instance of UserDetailsService containing the defined users
     *
    @Bean
    public UserDetailsService userDetailsService()
    {
        UserDetails user1 = User.withDefaultPasswordEncoder()
                .username("himanshu")
                .password("himanshu")
                .roles("ADMIN")
                .build();

        UserDetails user2 = User.withDefaultPasswordEncoder()
                .username("Admin")
                .password("himanshu")
                .roles("ADMIN")
                .build();

        UserDetails user3 = User.withDefaultPasswordEncoder()
                .username("User")
                .password("himanshu")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }
**/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain springsSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
        );
        http
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                        .requestMatchers("/api/v1/auth/register").permitAll()
                                .requestMatchers("/api/v1/auth/login").permitAll()
                                .requestMatchers("/api/v1/auth/refresh").permitAll()
                                .requestMatchers("/api/v1/auth/logout").permitAll()
                        .anyRequest().authenticated()
                )
//                .httpBasic(Customizer.withDefaults());
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(
                                (request, response, authException) -> {

                                    authException.printStackTrace(); // Log the exception for debugging
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    String message = "Unauthorized access: " + authException.getMessage();

                                    String errorMessage = (String) request.getAttribute("error");
                                    if (errorMessage != null) {
                                        message = errorMessage;
                                    }

                                    Map<String, Object> errorMap = Map.of(
                                            "message", message,
                                            "status", String.valueOf(401),
                                            "statusCode", Integer.toString(HttpServletResponse.SC_UNAUTHORIZED)
                                    );

                                    var objectMapper = new ObjectMapper();
                                    response.getWriter().write(
                                            objectMapper.writeValueAsString(errorMap)
                                    );
                                }
                        )
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }

}

