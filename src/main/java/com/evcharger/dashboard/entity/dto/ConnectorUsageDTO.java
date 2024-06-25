package com.evcharger.dashboard.entity.dto;

import lombok.Data;

@Data
public class ConnectorUsageDTO {
    private int hour;
    private double averageUsage;
}
