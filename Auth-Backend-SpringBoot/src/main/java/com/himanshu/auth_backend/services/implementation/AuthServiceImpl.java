package com.himanshu.auth_backend.services.implementation;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.services.AuthService;
import com.himanshu.auth_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
    private final UserService userService;
    @Override
    public UserResponseDto registerUser(UserRequestDto userRequestDto)
    {
        //Required Logic Here
        // Like Varifying The email
        // Varify Password
        // Default Role
        UserResponseDto user = userService.createUser(userRequestDto, null);
        return user;
    }
}
