package com.evcharger.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evcharger.dashboard.entity.Availability;

import java.util.List;

public interface AvailabilityService extends IService<Availability> {
    List<Availability> getAvailabilityByStationAndDate(String stationName, String date);
}
