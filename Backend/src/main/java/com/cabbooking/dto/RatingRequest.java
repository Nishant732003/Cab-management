package com.cabbooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for submitting a rating for a completed trip.
 *
 * Main Responsibilities:
 * - Encapsulates the rating value sent from the client to the server.
 * - Uses validation annotations to ensure the rating is within the valid range (1-5).
 *
 * Workflow:
 * - A customer sends a POST request with this DTO as the JSON body to rate a trip.
 * - The controller validates the request to ensure the rating is not null and is between 1 and 5.
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