package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.mapper.SiteMapper;
import com.evcharger.dashboard.service.SiteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site> implements SiteService {

//    @Override
//    public List<String> getAllUniqueCities() {
//        return baseMapper.getAllUniqueCityIds();
//    }

    @Override
    public List<String> getAllUniqueCityIds() {
        return baseMapper.getAllUniqueCityIds();
    }
}
