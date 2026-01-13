package com.himanshu.auth_backend.services.implementation;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.entities.Users;
import com.himanshu.auth_backend.repositories.UserRepository;
import com.himanshu.auth_backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService
{
    private final UserRepository userRepository;


    @Override
    public UserRequestDto createUser(UserRequestDto userRequestDto) {
        if (userRequestDto.getEmail() == null || userRequestDto.getEmail().isBlank())
        {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (UserRequestDto.existsByEmail(userRequestDto.getEmail()))
        {
            throw new IllegalArgumentException("User with email ID : " + userRequestDto.getEmail() + " already exists");
        }

    @Override
    public UserRequestDto getUserById(UUID id) {
        return null;
    }

    @Override
    public UserRequestDto getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserRequestDto updateUser(UUID id, UserRequestDto userRequestDto) {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public void setUserEnabled(UUID id, boolean enabled) {

    }

    @Override
    public List<UserRequestDto> getAllUsers() {
        return List.of();
    }
}
