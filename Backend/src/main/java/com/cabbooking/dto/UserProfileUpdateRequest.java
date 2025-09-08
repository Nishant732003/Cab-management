package com.cabbooking.dto;

/**
 * DTO for updating user profile information.
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
