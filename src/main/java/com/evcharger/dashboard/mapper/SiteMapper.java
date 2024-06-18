package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evcharger.dashboard.entity.Site;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SiteMapper extends BaseMapper<Site> {
    @Select("SELECT DISTINCT city FROM site")
    List<String> getAllUniqueCities();
}
