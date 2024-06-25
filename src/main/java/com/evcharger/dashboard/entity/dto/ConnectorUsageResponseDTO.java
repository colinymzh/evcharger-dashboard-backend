package com.evcharger.dashboard.entity.dto;

import com.evcharger.dashboard.entity.dto.ConnectorUsageDTO;
import lombok.Data;

import java.util.List;

@Data
public class ConnectorUsageResponseDTO {
    private String connectorId;
    private List<ConnectorUsageDTO> usageData;
}
