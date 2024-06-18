package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.StationSiteDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface StationMapper extends BaseMapper<Station> {

    @Select("SELECT s.station_name, si.city, si.street, si.postcode " +
            "FROM station s " +
            "JOIN site si ON s.site_id = si.site_id")
    IPage<StationSiteDTO> selectStationWithSite(Page<?> page);
}
