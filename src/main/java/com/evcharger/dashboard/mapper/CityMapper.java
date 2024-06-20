package com.evcharger.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.evcharger.dashboard.entity.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CityMapper extends BaseMapper<City> {

    @Select("SELECT city_name FROM city WHERE city_id = #{cityId}")
    String getCityNameById(String cityId);
}
