package com.himanshu.auth_backend.services;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // Method to create a new user
    UserRequestDto createUser(UserRequestDto userRequestDto);

    // Method to retrieve a user by ID
    UserRequestDto getUserById(UUID id);

    // Method to retrieve a user by email
    UserRequestDto getUserByEmail(String email);

    //Update user details
    UserRequestDto updateUser(UUID id, UserRequestDto userRequestDto);

    // Method to delete a user by ID
    void deleteUser(UUID id);

    // Method to enable or disable a user account
    void setUserEnabled(UUID id, boolean enabled);

    //get all users
    List<UserRequestDto> getAllUsers();

}
