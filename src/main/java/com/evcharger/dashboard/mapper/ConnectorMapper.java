package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evcharger.dashboard.entity.Connector;
import com.evcharger.dashboard.entity.dto.ConnectorDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ConnectorMapper extends BaseMapper<Connector> {
    @Select("SELECT station_name, connector_id, max_chargerate, plug_type, connector_type " +
            "FROM connector " +
            "WHERE station_name = #{stationName}")
    List<ConnectorDTO> getConnectorsByStationName(@Param("stationName") String stationName);


}
