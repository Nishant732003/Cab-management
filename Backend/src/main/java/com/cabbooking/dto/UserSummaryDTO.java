package com.cabbooking.dto;

/**
 * DTO for summarizing user information, including both customers and drivers.
 */
public class UserSummaryDTO {
    
    /*
     * The unique identifier for the user.
     */
    private Integer userId; 

    /*
     * The username of the user.
     */
    private String username;

    /*
     * The first name of the user.
     */
    private String firstName;

    /*
     * The last name of the user.
     */
    private String lastName;

    /*
     * The email address of the user.
     */
    private String email;

    /*
     * The mobile number of the user.
     */
    private String mobileNumber; 
    
    // Driver-specific fields (will be null for customers)

    /*
     * The average rating of the driver.
     */
    private Double rating;

    /*
     * The driver's license number.
     */
    private String licenceNo;

    /*
     * Indicates whether the driver's account is verified.
     */
    private Boolean verified;
    
    // ======= Constructors =======
    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Integer userId, String username, String firstName, String lastName, String email, String mobileNumber, Double rating, String licenceNo, Boolean verified) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.rating = rating;
        this.licenceNo = licenceNo;
        this.verified = verified;
    }

    // ======= Getters and Setters =======
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}