package com.evcharger.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evcharger.dashboard.entity.City;

public interface CityService extends IService<City> {
    String getCityNameById(String cityId);
}
