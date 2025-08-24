package com.cabbooking.dto;

// No need for email or password validation imports anymore
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.Size;
/**
 * DTO for handling partial updates to a user's profile. All fields are
 * optional; only non-null fields will be updated.
 *
 * NOTE: Username, email, and password are not updatable through this DTO to
 * ensure security and data consistency.
 */
public class UserProfileUpdateRequest {

    /**
     * The updated address of the user.
     */
    private String address;

    /**
     * The updated mobile number of the user.
     */
    private String mobileNumber;

    /**
     * The updated license number of the driver.
     */
    private String licenceNo;

    // ======= Getters and Setters =======
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }
}
