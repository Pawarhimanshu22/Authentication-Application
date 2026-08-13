package com.himanshu.auth_backend.controllers;

import com.himanshu.auth_backend.dtos.Request_DTOs.LoginRequestDto;
import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.TokenResponseDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.entities.Users;
import com.himanshu.auth_backend.repositories.UserRepository;
import com.himanshu.auth_backend.security.JwtService;
import com.himanshu.auth_backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto) {
        // Authenticate the user and generate a JWT token
        Authentication authentication = authenticateUser(loginRequestDto);
        Users user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid username and password"));

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
       //Generate Token
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
            throw new BadCredentialsException("Invalid username and password");

        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto userRequestDto)
    {
        UserResponseDto userResponseDto = authService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }
}
