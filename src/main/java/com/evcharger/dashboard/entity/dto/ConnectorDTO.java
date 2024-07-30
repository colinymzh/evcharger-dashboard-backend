package com.evcharger.dashboard.entity.dto;

import lombok.Data;

@Data
public class ConnectorDTO {
    private String stationName;
    private String connectorId;
    private Integer maxChargerate;
    private String plugType;
    private String connectorType;
}
