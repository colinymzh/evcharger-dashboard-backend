package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evcharger.dashboard.entity.Availability;
import com.evcharger.dashboard.entity.StationUsage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AvailabilityMapper extends BaseMapper<Availability> {

    @Select("SELECT * FROM availability WHERE station_name = #{stationName} AND date = #{date}")
    List<Availability> getAvailabilityByStationAndDate(@Param("stationName") String stationName, @Param("date") String date);

    @Select("SELECT * FROM availability WHERE station_name = #{stationName} AND date >= (SELECT MAX(date) - INTERVAL #{scope} DAY FROM availability) ORDER BY date, hour")
    List<Availability> getAvailabilityByStationAndScope(@Param("stationName") String stationName, @Param("scope") int scope);

    @Select("SELECT * FROM availability WHERE station_name = #{stationName}")
    List<Availability> getAvailabilityByStationName(@Param("stationName") String stationName);

    @Select("SELECT DISTINCT city_id FROM availability WHERE station_name = #{stationName}")
    Integer getCityIdByStationName(@Param("stationName") String stationName);

    @Select("SELECT * FROM availability WHERE city_id = #{cityId}")
    List<Availability> getAvailabilityByCityId(@Param("cityId") Integer cityId);

    @Select("SELECT city_name FROM city WHERE city_id = #{cityId}")
    String getCityNameByCityId(@Param("cityId") Integer cityId);

    @Select("SELECT station_name, " +
            "COUNT(*) as total_count, " +
            "SUM(CASE WHEN is_available = 0 THEN 1 ELSE 0 END) as unavailable_count " +
            "FROM availability " +
            "WHERE date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY station_name")
    List<StationUsage> getStationUsageForLastWeek(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Select("SELECT MAX(date) FROM availability")
    LocalDate getLatestDate();
}
