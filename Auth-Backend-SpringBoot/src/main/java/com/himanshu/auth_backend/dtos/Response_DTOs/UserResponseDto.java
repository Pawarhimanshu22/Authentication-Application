package com.himanshu.auth_backend.dtos.Response_DTOs;

import com.himanshu.auth_backend.dtos.Response_DTOs.AddressResponseDto;
import com.himanshu.auth_backend.dtos.Response_DTOs.RoleResponseDto;
import com.himanshu.auth_backend.entities.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private UUID id;
    private String email;
    private String name;
    private String image;
    private String gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String bio;
    private boolean enable;
    private Instant createdAt;
    private Instant updatedAt;
    private Provider provider;
    private Set<RoleResponseDto> roles;
    private List<AddressResponseDto> addresses;
}