package com.evcharger.dashboard.mapper;

import com.evcharger.dashboard.entity.dto.ConnectorData;
import com.evcharger.dashboard.entity.dto.SiteCoordinates;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PredictionInputMapper {

    @Select("SELECT coordinates_x, coordinates_y FROM predictioninput WHERE station_name = #{stationName} LIMIT 1")
    SiteCoordinates getCoordinatesByStationName(@Param("stationName") String stationName);

    @Select("SELECT * FROM predictioninput WHERE station_name = #{stationName}")
    List<ConnectorData> getConnectorsByStationName(@Param("stationName") String stationName);

}
