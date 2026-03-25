package com.himanshu.auth_backend.services.implementation;

import com.himanshu.auth_backend.dtos.Request_DTOs.AddressRequestDto;
import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.UserResponseDto;
import com.himanshu.auth_backend.entities.Provider;
import com.himanshu.auth_backend.entities.Users;
import com.himanshu.auth_backend.exceptions.ResourceNotFoundException;
import com.himanshu.auth_backend.repositories.UserRepository;
import com.himanshu.auth_backend.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto, Provider provider) {

        // Null check for the entire request
        if (userRequestDto == null) {
            throw new IllegalArgumentException("User request cannot be null");
        }

        // Name checks
        if (userRequestDto.getName() == null || userRequestDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        // Email checks
        if (userRequestDto.getEmail() == null || userRequestDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!userRequestDto.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Password checks
        if (userRequestDto.getPassword() == null || userRequestDto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRequestDto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        Users user = modelMapper.map(userRequestDto, Users.class);

        user.setProvider(user.getProvider() != null ?  user.getProvider() : provider.LOCAL);

        //Here Role Assignment to new user for Authorization purpose
        Users savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Transactional
    @Override
    public Iterable<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class))
                .toList();
    }

    @Override
    public UserResponseDto getUserByEmail(String email)
    {
        Users users = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        return modelMapper.map(users, UserResponseDto.class);
    }

    @Override
    public UserResponseDto getUserById(UUID id)
    {
        Users users = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return modelMapper.map(users, UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUser(UUID id, UserRequestDto userRequestDto, AddressRequestDto addressRequestDto) {
        Users existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        // Update fields if they are provided in the request
        if (userRequestDto.getName() != null && !userRequestDto.getName().trim().isEmpty())
        {
            existingUser.setName(userRequestDto.getName());
        }
        if (userRequestDto.getEmail() != null && !userRequestDto.getEmail().trim().isEmpty())
        {
            if (!userRequestDto.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            {
                throw new IllegalArgumentException("Email format is invalid");
            }
            if (userRepository.existsByEmail(userRequestDto.getEmail()) && !existingUser.getEmail().equals(userRequestDto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            existingUser.setEmail(userRequestDto.getEmail());
        }

        //TODO Change the password Updation Logic
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().trim().isEmpty())
        {
            if (userRequestDto.getPassword().length() < 8)
            {
                throw new IllegalArgumentException("Password must be at least 8 characters");
            }


            existingUser.setPassword(userRequestDto.getPassword());
        }

        // Here you can also update the address fields if needed using addressRequestDto

        Users updatedUser = userRepository.save(existingUser);
        return modelMapper.map(updatedUser, UserResponseDto.class);
    }

    @Override
    public void deleteUser(UUID id)
    {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void setUserEnabled(UUID id, boolean enabled) {

    }
}
