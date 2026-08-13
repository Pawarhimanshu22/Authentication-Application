package com.himanshu.auth_backend.dtos.Request_DTOs;

public record LoginRequestDto(
    String email,
    String password
) {
}
