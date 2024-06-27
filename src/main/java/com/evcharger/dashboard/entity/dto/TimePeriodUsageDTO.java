package com.evcharger.dashboard.entity.dto;

import lombok.Data;

@Data
public class TimePeriodUsageDTO {
    private String timePeriod;
    private double averageUsage;
}
