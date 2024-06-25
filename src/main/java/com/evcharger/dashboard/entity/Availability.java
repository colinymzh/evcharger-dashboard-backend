package com.evcharger.dashboard.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("availability")
public class Availability {
    @TableId
    private String stationName;
    private Integer connectorId;
    private String date;
    private Integer hour;
    private Boolean isAvailable;
    private String cityId;
}
