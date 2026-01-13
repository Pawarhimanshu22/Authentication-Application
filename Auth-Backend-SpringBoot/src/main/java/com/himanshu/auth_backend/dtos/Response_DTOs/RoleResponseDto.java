package com.himanshu.auth_backend.dtos.Response_DTOs;

import com.himanshu.auth_backend.entities.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

// Use this for sending role data back to client
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {

    private UUID id;
    private RoleName roleName;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}