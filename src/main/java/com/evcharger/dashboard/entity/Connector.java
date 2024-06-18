package com.evcharger.dashboard.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("connector")
public class Connector {
    @TableId
    private String stationName;
    private String connectorId;
    private Integer maxChargerrate;
    private String plugType;
    private String connectorType;
}
