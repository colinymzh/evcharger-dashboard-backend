package com.evcharger.dashboard.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("station")
public class Station {
    @TableId
    private String stationName;
    private String siteId;
    private Double tariffAmount;
    private String tariffDescription;
    private Double tariffConnectionfee;
}
