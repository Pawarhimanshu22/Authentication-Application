package com.himanshu.auth_backend.dtos.Request_DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


// Use this for both CREATE and UPDATE operations
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image;

    @Size(max = 20, message = "Gender must not exceed 20 characters")
    private String gender;

    @Pattern(regexp = "^[0-9]{10,20}$", message = "Phone number should be valid")
    private String phoneNumber;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;
}