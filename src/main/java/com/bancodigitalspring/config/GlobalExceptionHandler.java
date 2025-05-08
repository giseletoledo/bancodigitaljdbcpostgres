package com.bancodigitalspring.config;

import com.bancodigitalspring.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Tratamento para IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        String errorCode = "BAD_REQUEST";

        logger.error("Erro de validação: {} - Path: {}", ex.getMessage(), path, ex);

        ErrorResponse error = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                path
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Tratamento para SQLException
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(
            SQLException ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        String errorCode = "DATABASE_ERROR";

        logger.error("Erro de banco de dados: {} - Path: {}", ex.getMessage(), path, ex);

        ErrorResponse error = new ErrorResponse(
                errorCode,
                "Ocorreu um erro no banco de dados",
                path
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Tratamento genérico para outras exceções
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();
        String errorCode = "INTERNAL_ERROR";

        logger.error("Erro interno: {} - Path: {}", ex.getMessage(), path, ex);

        ErrorResponse error = new ErrorResponse(
                errorCode,
                "Ocorreu um erro interno no servidor",
                path
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}