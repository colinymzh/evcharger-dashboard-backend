package com.evcharger.dashboard.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyCityUsageDTO {
    private String dayOfWeek;
    private double averageUsage;
}

