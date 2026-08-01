package dev.project.urlshortener.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.project.urlshortener.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRunTimeException(UrlNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                e.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidException(MethodArgumentNotValidException e,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                e.getBindingResult().getFieldError().getDefaultMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<ErrorResponse> handleUrlExpiredException(UrlExpiredException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.GONE;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(CustomAliasAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCustomAliasAlreadyExistsException(CustomAliasAlreadyExistsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                e.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
