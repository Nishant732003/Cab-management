package com.cabbooking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/*
 * BlacklistedToken entity to store invalidated tokens for security purposes.
 */
@Entity
public class BlacklistedToken {

    /*
     * Represents a token that has been invalidated due to logout or
     * other security measures.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * The token itself.
     */
    private String token;

    // ======= Getters and Setters =======
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
