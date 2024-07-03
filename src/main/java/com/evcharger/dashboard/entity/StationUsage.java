package com.evcharger.dashboard.entity;

import lombok.Data;


@Data
public class StationUsage {
    private String stationName;
    private Integer totalCount;
    private Integer unavailableCount;
}

