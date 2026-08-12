package com.himanshu.auth_backend.services.implementation;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.services.AuthService;
import com.himanshu.auth_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public UserResponseDto registerUser(UserRequestDto userRequestDto)
    {
        //Required Logic Here
        // Like Varifying The email
        // Varify Password
        // Default Role
        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        UserResponseDto user = userService.createUser(userRequestDto, null);
        return user;
    }
}
