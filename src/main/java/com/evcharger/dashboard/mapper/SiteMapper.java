package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evcharger.dashboard.entity.Site;
import com.evcharger.dashboard.entity.dto.SiteCoordinates;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SiteMapper extends BaseMapper<Site> {

    @Select("SELECT DISTINCT city_id FROM site")
    List<String> getAllUniqueCityIds();

    @Select("SELECT coordinates_x, coordinates_y FROM site WHERE site_id = #{siteId}")
    SiteCoordinates getCoordinatesBySiteId(@Param("siteId") Integer siteId);
}
