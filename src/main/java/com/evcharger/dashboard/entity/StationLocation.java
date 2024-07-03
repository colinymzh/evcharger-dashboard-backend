package com.evcharger.dashboard.entity;


import lombok.Data;

@Data
public class StationLocation {
    private String stationName;
    private Double coordinatesX;
    private Double coordinatesY;
}

