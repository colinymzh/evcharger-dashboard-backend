package com.evcharger.dashboard.entity.dto;

import lombok.Data;

@Data
public class ConnectorData {
    private String stationName;
    private int connectorId;
    private double coordinatesX;
    private double coordinatesY;
    private double tariffAmount;
    private Double tariffConnectionfee;
    private double maxChargerate;
    private int plugTypeCcs;
    private int plugTypeChademo;
    private int plugTypeType2Plug;
    private int connectorTypeAc;
    private int connectorTypeAcControllerReceiver;
    private int connectorTypeRapid;
    private int connectorTypeUltraRapid;
    private int connectorTypeICharging;
    private double connectorAvgUsage;
    private double stationAvgUsage;
    private double distanceToCenter;
    private double cityStationDensity;
    private int stationConnectorCount;
    private double stationAvgMaxChargerate;
    private double stationDensity10km;
    private double stationDensity1km;
    private double stationDensity20km;
    private int cityId;
}
