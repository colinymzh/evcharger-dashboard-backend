package com.evcharger.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.evcharger.dashboard.entity.Site;

import java.util.List;

public interface SiteService extends IService<Site> {

    //List<String> getAllUniqueCities();

    List<String> getAllUniqueCityIds();

    List<String> getAllUniqueCityIds1();
    boolean save(Site site);
    Site getById(String id);
}
