package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evcharger.dashboard.entity.Station;
import com.evcharger.dashboard.entity.StationDetailDTO;
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

    @Select("<script>" +
            "SELECT s.station_name, si.city, si.street, si.postcode " +
            "FROM station s " +
            "JOIN site si ON s.site_id = si.site_id " +
            "JOIN connector c ON s.station_name = c.station_name " +
            "WHERE 1=1 " +
            "<if test='stationName != null and stationName != \"\"'> " +
            "AND s.station_name LIKE CONCAT('%', #{stationName}, '%') " +
            "</if> " +
            "<if test='city != null and city != \"\"'> " +
            "AND si.city LIKE CONCAT('%', #{city}, '%') " +
            "</if> " +
            "<if test='postcode != null and postcode != \"\"'> " +
            "AND si.postcode LIKE CONCAT('%', #{postcode}, '%') " +
            "</if> " +
            "<if test='supportsFastCharging != null'> " +
            "AND c.connector_type = 'Rapid' " +
            "</if> " +
            "GROUP BY s.station_name " +
            "</script>")
    IPage<StationSiteDTO> selectStationWithFilters(Page<?> page,
                                                   @Param("stationName") String stationName,
                                                   @Param("city") String city,
                                                   @Param("postcode") String postcode,
                                                   @Param("supportsFastCharging") Boolean supportsFastCharging);

    @Select("SELECT s.station_name, s.site_id, s.tariff_amount, s.tariff_description, s.tariff_connectionfee, " +
            "si.city, si.street, si.postcode " +
            "FROM station s " +
            "JOIN site si ON s.site_id = si.site_id " +
            "WHERE s.station_name = #{stationName}")
    StationDetailDTO getStationDetails(@Param("stationName") String stationName);

}
