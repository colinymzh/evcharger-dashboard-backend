package com.evcharger.dashboard.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("city")
public class City {
    @TableId
    private String cityId;
    private String cityName;
}
