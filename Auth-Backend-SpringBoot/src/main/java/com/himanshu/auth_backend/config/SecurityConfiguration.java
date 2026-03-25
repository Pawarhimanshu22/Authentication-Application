package com.himanshu.auth_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfiguration {

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
}
