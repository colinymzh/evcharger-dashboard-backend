package com.evcharger.dashboard.entity.dto;

import lombok.Data;

@Data
public class WeeklyUsageDTO {
    private String dayOfWeek;
    private double averageUsage;
}
