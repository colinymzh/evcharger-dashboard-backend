package com.evcharger.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.mapper.SiteMapper;
import com.evcharger.dashboard.service.SiteService;
import org.springframework.stereotype.Service;

@Service
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site> implements SiteService {
}
