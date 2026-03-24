package com.himanshu.auth_backend.controllers;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.entities.Provider;
import com.himanshu.auth_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Iterable<UserResponseDto>> getAllUsers()
    {
        Iterable<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    public ResponseEntity<UserResponseDto> getUserByEmail(String email) {
        return null;
    }

    public ResponseEntity<UserResponseDto> getUserById(String id) {
        return null;
    }
    public ResponseEntity<UserResponseDto> getUserByEmailAndProvider(String email, String provider) {
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.createUser(userRequestDto, Provider.LOCAL);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
