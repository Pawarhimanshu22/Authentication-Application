package com.himanshu.auth_backend.dtos.Request_DTOs;

import com.himanshu.auth_backend.entities.RoleName;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// Use this for both CREATE and UPDATE operations
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {

    @NotNull(message = "Role name is required")
    private RoleName roleName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}