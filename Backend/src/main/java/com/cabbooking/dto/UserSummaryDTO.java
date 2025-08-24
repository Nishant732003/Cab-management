package com.cabbooking.dto;

/**
 * A DTO to represent a summary of a user, containing only essential,
 * non-sensitive information. This is used for admin-facing lists of users.
 */
public class UserSummaryDTO {

    /*
     * The unique identifier of the user
     */
    private Integer id;

    /*
     * The username of the user
     */
    private String username;

    /*
     * The email address of the user
     */
    private String email;

    public UserSummaryDTO(Integer id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // ======= Getters and Setters =======
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
