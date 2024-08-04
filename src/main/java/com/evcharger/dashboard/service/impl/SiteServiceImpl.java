package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.mapper.SiteMapper;
import com.evcharger.dashboard.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site> implements SiteService {


    @Override
    public List<String> getAllUniqueCityIds() {
        return baseMapper.getAllUniqueCityIds();
    }

    @Autowired
    private SiteMapper siteMapper;

    @Override
    public List<String> getAllUniqueCityIds1() {
        return siteMapper.getAllUniqueCityIds();
    }

    @Override
    public boolean save(Site site) {
        return siteMapper.insert(site) > 0;
    }

    @Override
    public Site getById(String id) {
        return siteMapper.selectById(id);
    }
}
