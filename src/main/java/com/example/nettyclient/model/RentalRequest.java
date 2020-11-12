package com.example.nettyclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RentalRequest {

    @JsonProperty("id")
    private String id;

    @JsonProperty("car_type")
    private String carType;

    @JsonProperty("color")
    private String color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
