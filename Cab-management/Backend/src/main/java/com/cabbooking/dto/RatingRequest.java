package com.cabbooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for submitting a rating for a trip.
 */
public class RatingRequest {

    /**
     * The numerical rating given by the customer for a trip.
     *
     * Constraints:
     * - Must not be null.
     * - Must be a minimum value of 1.
     * - Must be a maximum value of 5.
     */
    @NotNull(message = "Rating value is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    // ======= Getters and Setters =======

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}