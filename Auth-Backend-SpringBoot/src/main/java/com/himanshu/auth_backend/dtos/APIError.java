package com.himanshu.auth_backend.dtos;

import java.time.OffsetDateTime;

public record APIError(
        int status,
        String error,
        String message,
        String path,
        OffsetDateTime timestamp
) {
    public APIError(int status, String error, String message, String path) {
        this(status, error, message, path, OffsetDateTime.now());
    }

    public APIError(int status, String error, String message) {
        this(status, error, message, null, OffsetDateTime.now());
    }

    public APIError(int status, String message) {
        this(status, null, message, null, OffsetDateTime.now());
    }
}
