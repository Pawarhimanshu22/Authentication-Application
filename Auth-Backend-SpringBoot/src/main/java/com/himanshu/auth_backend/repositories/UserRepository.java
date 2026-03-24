package com.himanshu.auth_backend.repositories;

import com.himanshu.auth_backend.dtos.Request_DTOs.UserRequestDto;
import com.himanshu.auth_backend.entities.Users;
import jakarta.validation.constraints.Email;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    // Find a user by their email address
   Optional<Users> findByEmail(String email);

    // Check if a user exists by their email address
    boolean existsByEmail(String email);

}
