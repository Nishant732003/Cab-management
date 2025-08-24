package com.cabbooking.dto;

/**
 * DTO for providing a fare estimate to the customer. It includes the car type
 * and the calculated minimum and maximum possible fares for a given distance
 * based on the available cabs of that type.
 */
public class FareEstimateResponse {

    /*
     * The car type for which the fare estimate is being provided.
     */
    private String carType;

    /*
     * The minimum possible fare for the specified car type and distance.
     */
    private float minFare;

    /*
     * The maximum possible fare for the specified car type and distance.
     */
    private float maxFare;

    public FareEstimateResponse(String carType, float minFare, float maxFare) {
        this.carType = carType;
        this.minFare = minFare;
        this.maxFare = maxFare;
    }

    // ======= Getters and Setters =======
    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public float getMinFare() {
        return minFare;
    }

    public void setMinFare(float minFare) {
        this.minFare = minFare;
    }

    public float getMaxFare() {
        return maxFare;
    }

    public void setMaxFare(float maxFare) {
        this.maxFare = maxFare;
    }
}
