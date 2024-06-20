package com.evcharger.dashboard.entity;

import lombok.Data;

@Data
public class StationSiteDTO {
    private String stationName;
    private String cityId;
    private String cityName;
    private String street;
    private String postcode;
}