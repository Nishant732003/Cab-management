package com.cabbooking.exception;

import com.cabbooking.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler centralizes exception handling for the entire
 * application.
 *
 * It intercepts uncaught exceptions across all controllers and REST endpoints,
 * logs detailed error information including stack traces, HTTP method, and
 * request URI, and returns a standardized error response with accurate HTTP
 * status code.
 *
 * This improves error traceability and provides consistent feedback to API
 * clients, especially for authentication and token-related errors.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all uncaught exceptions not explicitly handled elsewhere. Logs
     * full stack trace with request details. Returns a standardized error
     * response with HTTP 500 status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(HttpServletRequest request, Exception ex) {
        logger.error("Exception occurred at [{} {}]: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        String detailedMessage = buildErrorMessageWithSource(ex);

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                detailedMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles custom AuthenticationException thrown when authentication fails.
     * Returns HTTP 401 Unauthorized with error details.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(HttpServletRequest request, AuthenticationException ex) {
        logger.warn("Authentication failure at [{} {}]: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles JWT exceptions, e.g., invalid, expired, malformed tokens. Returns
     * HTTP 401 Unauthorized to prompt re-authentication.
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(HttpServletRequest request, JwtException ex) {
        logger.warn("JWT error at [{} {}]: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Invalid or expired JWT token",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Builds a detailed error message from the exception including the file and
     * line number where the error originated, for easier troubleshooting.
     */
    private String buildErrorMessageWithSource(Exception ex) {
        StackTraceElement[] stack = ex.getStackTrace();
        if (stack.length > 0) {
            StackTraceElement source = stack[0];
            return String.format("%s (at %s:%d)", ex.getMessage(), source.getFileName(), source.getLineNumber());
        } else {
            return ex.getMessage();
        }
    }
}
