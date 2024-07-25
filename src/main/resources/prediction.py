import argparse

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--stationName", required=True)
    parser.add_argument("--connectorId", required=True)
    parser.add_argument("--coordinatesX", required=True)
    parser.add_argument("--coordinatesY", required=True)
    parser.add_argument("--tariffAmount", required=True)
    parser.add_argument("--tariffConnectionfee", required=True)
    parser.add_argument("--maxChargerate", required=True)
    parser.add_argument("--plugTypeCcs", required=True)
    parser.add_argument("--plugTypeChademo", required=True)
    parser.add_argument("--plugTypeType2Plug", required=True)
    parser.add_argument("--connectorTypeAc", required=True)
    parser.add_argument("--connectorTypeAcControllerReceiver", required=True)
    parser.add_argument("--connectorTypeRapid", required=True)
    parser.add_argument("--connectorTypeUltraRapid", required=True)
    parser.add_argument("--connectorTypeICharging", required=True)
    parser.add_argument("--connectorAvgUsage", required=True)
    parser.add_argument("--stationAvgUsage", required=True)
    parser.add_argument("--distanceToCenter", required=True)
    parser.add_argument("--cityStationDensity", required=True)
    parser.add_argument("--stationConnectorCount", required=True)
    parser.add_argument("--stationAvgMaxChargerate", required=True)
    parser.add_argument("--stationDensity10km", required=True)
    parser.add_argument("--stationDensity1km", required=True)
    parser.add_argument("--stationDensity20km", required=True)
    parser.add_argument("--dayOfWeek", required=True)
    parser.add_argument("--hour", required=True)
    parser.add_argument("--weather", required=True)

    args = parser.parse_args()

    # 预测逻辑
    prediction = f"Prediction for connector {args.connectorId} of station {args.stationName} with weather {args.stationAvgUsage}"

    print(prediction)

if __name__ == "__main__":
    main()
