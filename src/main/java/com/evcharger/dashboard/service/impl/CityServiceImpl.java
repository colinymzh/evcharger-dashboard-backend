package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.City;
import com.evcharger.dashboard.mapper.CityMapper;
import com.evcharger.dashboard.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Autowired
    private CityMapper cityMapper;

    @Override
    public String getCityNameById(String cityId) {
        return cityMapper.getCityNameById(cityId);
    }
}
