package com.himanshu.auth_backend.controllers;

import com.himanshu.auth_backend.dtos.Request_DTOs.AddressRequestDto;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.createUser(userRequestDto, Provider.LOCAL);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    public ResponseEntity<Iterable<UserResponseDto>> getAllUsers()
    {
        Iterable<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email)
    {
        UserResponseDto userByEmail = userService.getUserByEmail(email);
        if (userByEmail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userByEmail);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponseDto> getUserByID(@PathVariable UUID id)
    {
        UserResponseDto userById = userService.getUserById(id);
        if (userById == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userById);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id, @RequestBody UserRequestDto userRequestDto, AddressRequestDto addressRequestDto)
    {
        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto, addressRequestDto);
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id)
    {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
