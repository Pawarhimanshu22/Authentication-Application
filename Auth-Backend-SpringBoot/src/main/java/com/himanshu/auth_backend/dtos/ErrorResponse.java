package com.himanshu.auth_backend.dtos;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor   // ✅ important (missing tha)
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private String error;
    private String message;
    private String path;

}