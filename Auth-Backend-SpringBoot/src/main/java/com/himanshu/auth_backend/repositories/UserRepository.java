package com.himanshu.auth_backend.repositories;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    // Find a user by their email address
   Optional<UserRequestDto> findByEmail(String email);
    // Check if a user exists by their email address
    boolean existsById(String email);
}
