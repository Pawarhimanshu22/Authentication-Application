package com.himanshu.auth_backend.controllers;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRequestDto userRequestDto)
    {
        UserResponseDto userResponseDto = authService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }
}
