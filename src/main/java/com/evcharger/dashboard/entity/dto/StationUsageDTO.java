package com.evcharger.dashboard.entity.dto;

import lombok.Data;

@Data
public class StationUsageDTO {
    private String stationName;
    private Double averageUsage;
    private Integer usageLevel;
    private Double coordinatesX;
    private Double coordinatesY;
}
