package com.evcharger.dashboard.entity.dto;

import lombok.Data;

import java.util.List;

@Data
public class WeeklyHourlyUsageDTO {
    private String dayOfWeek;
    private List<HourlyUsageDTO> hourlyUsage;
}
