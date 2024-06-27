package com.evcharger.dashboard.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class HourlyUsageDTO {
    private int hour;
    private double averageUsage;
}

