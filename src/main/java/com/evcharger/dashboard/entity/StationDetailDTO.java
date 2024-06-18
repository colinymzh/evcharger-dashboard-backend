package com.evcharger.dashboard.entity;

import lombok.Data;

import java.util.List;

@Data
public class StationDetailDTO {
    private String stationName;
    private String siteId;
    private Double tariffAmount;
    private String tariffDescription;
    private Double tariffConnectionfee;
    private String city;
    private String street;
    private String postcode;
    private List<ConnectorDTO> connectors;
}
