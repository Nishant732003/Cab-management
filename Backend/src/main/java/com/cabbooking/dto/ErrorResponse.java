package com.cabbooking.dto;

import java.time.LocalDateTime;

/**
 * DTO for sending error responses in a standardized format.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Default constructor needed for frameworks.
     */
    public ErrorResponse() {
    }

    /**
     * Creates a new error response instance, capturing the current timestamp.
     *
     * @param status HTTP status code
     * @param error HTTP status phrase
     * @param message Detailed error message
     * @param path Request path causing the error
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // ======= Getters and Setters =======
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
