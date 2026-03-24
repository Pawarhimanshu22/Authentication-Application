package com.himanshu.auth_backend.services;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.entities.Provider;

import java.util.List;
import java.util.UUID;

public interface UserService {

    /**
     * Creates a new user based on the provided request data.
     *
     * @param userRequestDto the DTO containing the user's details (e.g., name, email, password)
     * @return a {@link UserResponseDto} representing the newly created user
     */
    UserResponseDto createUser(UserRequestDto userRequestDto, Provider provider);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the {@link UUID} of the user to retrieve
     * @return a {@link UserResponseDto} representing the found user
     * @throws UserNotFoundException if no user exists with the given ID
     */
    UserResponseDto getUserById(UUID id);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return a {@link UserResponseDto} representing the found user
     * @throws UserNotFoundException if no user exists with the given email
     */
    UserResponseDto getUserByEmail(String email);

    /**
     * Updates an existing user's details with the provided request data.
     *
     * @param id             the {@link UUID} of the user to update
     * @param userRequestDto the DTO containing the updated user details
     * @return a {@link UserResponseDto} representing the updated user
     * @throws UserNotFoundException if no user exists with the given ID
     */
    UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto);

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id the {@link UUID} of the user to delete
     * @throws UserNotFoundException if no user exists with the given ID
     */
    void deleteUser(UUID id);

    /**
     * Enables or disables a user account.
     *
     * @param id      the {@link UUID} of the user to update
     * @param enabled {@code true} to enable the user account, {@code false} to disable it
     * @throws UserNotFoundException if no user exists with the given ID
     */
    void setUserEnabled(UUID id, boolean enabled);

    /**
     * Retrieves a list of all registered users.
     *
     * @return a {@link List} of {@link UserResponseDto} representing all users,
     *         or an empty list if no users exist
     */
    Iterable<UserResponseDto> getAllUsers();
}