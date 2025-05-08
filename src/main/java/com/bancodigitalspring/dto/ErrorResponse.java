package com.bancodigitalspring.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        String path,
        List<String> details
) {
    public ErrorResponse(String code, String message, String path) {
        this(code, message, LocalDateTime.now(), path, null);
    }

    public ErrorResponse(String code, String message, String path, List<String> details) {
        this(code, message, LocalDateTime.now(), path, details);
    }
}