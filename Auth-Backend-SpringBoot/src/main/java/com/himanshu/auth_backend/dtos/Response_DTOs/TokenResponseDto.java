package com.himanshu.auth_backend.dtos.Response_DTOs;

public record TokenResponseDto(
    String accessToken,
    String refreshToken,
    long expiresIn,
    String tokenType,
    String token,
    UserResponseDto userResponseDto
) {
    public static TokenResponseDto of(String accessToken, String refreshToken, long expiresIn, String token, UserResponseDto userResponseDto) {
        return new TokenResponseDto(accessToken, refreshToken, expiresIn, "Bearer", token, userResponseDto);
    }
}
