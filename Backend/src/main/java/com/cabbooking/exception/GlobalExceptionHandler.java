package com.cabbooking.exception;

import com.cabbooking.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler centralizes exception handling for the entire application.
 * 
 * It intercepts uncaught exceptions across all controllers and REST endpoints,
 * logs detailed error information including stack traces, HTTP method, and request URI,
 * and returns a standardized error response with accurate HTTP status code.
 * 
 * This improves error traceability and provides consistent feedback to API clients.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles all exceptions not explicitly handled elsewhere.
     * Logs full stack trace with request details.
     * Returns a structured JSON error response with timestamp, error status, message, and path.
     * 
     * @param request HTTP request information to extract URI and method
     * @param ex The uncaught exception thrown during processing
     * @return ResponseEntity with ErrorResponse body and HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(HttpServletRequest request, Exception ex) {
        // Log exception with full stack trace
        logger.error("Exception occurred at [{} {}]: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        // Build a detailed error message including source file and line number
        String detailedMessage = buildErrorMessageWithSource(ex);

        // Prepare standardized error response DTO
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            detailedMessage,
            request.getRequestURI()
        );

        // Return the error response with HTTP 500 status code
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Builds a detailed error message from exception including file and line number where the error originated,
     * for easier troubleshooting.
     * 
     * @param ex The exception from which to extract information
     * @return A formatted string with message, file name, and line number
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
