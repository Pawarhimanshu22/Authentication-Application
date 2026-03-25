package com.himanshu.auth_backend.services;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;

public interface AuthService
{
    UserResponseDto registerUser(UserRequestDto userRequestDto);
//    UserResponseDto loginUser(UserRequestDto userRequestDto);
}
