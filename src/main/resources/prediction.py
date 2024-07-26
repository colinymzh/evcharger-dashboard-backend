import argparse
import numpy as np
import pandas as pd
import joblib
import os
from sklearn.preprocessing import LabelEncoder

def encode_cyclical_features(value, max_value):
    sin_value = np.sin(2 * np.pi * value / max_value)
    cos_value = np.cos(2 * np.pi * value / max_value)
    return sin_value, cos_value
    

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--connectorId", required=True, type=int)
    parser.add_argument("--coordinatesX", required=True, type=float)
    parser.add_argument("--coordinatesY", required=True, type=float)
    parser.add_argument("--tariffAmount", required=True, type=float)
    parser.add_argument("--tariffConnectionfee", required=True, type=float)
    parser.add_argument("--maxChargerate", required=True, type=float)
    parser.add_argument("--plugTypeCcs", required=True, type=int)
    parser.add_argument("--plugTypeChademo", required=True, type=int)
    parser.add_argument("--plugTypeType2Plug", required=True, type=int)
    parser.add_argument("--connectorTypeAc", required=True, type=int)
    parser.add_argument("--connectorTypeAcControllerReceiver", required=True, type=int)
    parser.add_argument("--connectorTypeRapid", required=True, type=int)
    parser.add_argument("--connectorTypeUltraRapid", required=True, type=int)
    parser.add_argument("--connectorTypeICharging", required=True, type=int)
    parser.add_argument("--weather", required=True)
    parser.add_argument("--connectorAvgUsage", required=True, type=float)
    parser.add_argument("--stationAvgUsage", required=True, type=float)
    parser.add_argument("--distanceToCenter", required=True, type=float)
    parser.add_argument("--cityStationDensity", required=True, type=float)
    parser.add_argument("--stationConnectorCount", required=True, type=int)
    parser.add_argument("--stationAvgMaxChargerate", required=True, type=float)
    parser.add_argument("--stationDensity10km", required=True, type=float)
    parser.add_argument("--stationDensity1km", required=True, type=float)
    parser.add_argument("--stationDensity20km", required=True, type=float)
    parser.add_argument("--stationName", required=True)
    parser.add_argument("--cityId", required=True)
    parser.add_argument("--hour", required=True, type=int)
    parser.add_argument("--dayOfWeek", required=True, type=int)

    args = parser.parse_args()
    
    
    expected_feature_names = [
        'connector_id', 'coordinates_x', 'coordinates_y', 'tariff_amount', 'tariff_connectionfee',
        'max_chargerate', 'plug_type_ccs', 'plug_type_chademo', 'plug_type_type_2_plug',
        'connector_type_AC', 'connector_type_AC Controller/Receiver', 'connector_type_Rapid',
        'connector_type_Ultra-Rapid', 'connector_type_iCharging', 'weather_Clear', 'weather_Clouds',
        'weather_Drizzle', 'weather_Fog', 'weather_Haze', 'weather_Mist', 'weather_Rain',
        'connector_avg_usage', 'station_avg_usage', 'distance_to_center', 'city_station_density',
        'station_connector_count', 'station_avg_max_chargerate', 'station_density_10km',
        'station_density_1km', 'station_density_20km', 'station_name_encoded', 'city_id_encoded',
        'hour_sin', 'hour_cos', 'day_of_week_sin', 'day_of_week_cos'
    ]
    
# 定义绝对路径
    base_directory = r"C:\Users\colin\code\evcharger-anomaly-detection"  # 请替换为实际的基础目录路径
    model_directory = os.path.join(base_directory, 'saved_models')
    model_filename = 'random_forest_model.joblib'
    model_path = os.path.join(model_directory, model_filename)
    
# 加载模型
    loaded_rf = joblib.load(model_path)

   
    
    # 处理循环特征
    hour_sin, hour_cos = encode_cyclical_features(args.hour, 24)
    day_of_week_sin, day_of_week_cos = encode_cyclical_features(args.dayOfWeek, 7)

# 加载预先训练好的LabelEncoder
    station_encoder_path = os.path.join(base_directory, 'station_encoder.joblib')
    city_encoder_path = os.path.join(base_directory, 'city_encoder.joblib')
    station_encoder = joblib.load(station_encoder_path)
    city_encoder = joblib.load(city_encoder_path)
    
    station_name_encoded = station_encoder.transform([str(args.stationName)])[0]
    city_id_encoded = city_encoder.transform([str(args.cityId)])[0]
    
    # 使用字典来创建特征
    features_dict = {
        'connector_id': args.connectorId,
        'coordinates_x': args.coordinatesX,
        'coordinates_y': args.coordinatesY,
        'tariff_amount': args.tariffAmount,
        'tariff_connectionfee': args.tariffConnectionfee,
        'max_chargerate': args.maxChargerate,
        'plug_type_ccs': args.plugTypeCcs,
        'plug_type_chademo': args.plugTypeChademo,
        'plug_type_type_2_plug': args.plugTypeType2Plug,
        'connector_type_AC': args.connectorTypeAc,
        'connector_type_AC Controller/Receiver': args.connectorTypeAcControllerReceiver,
        'connector_type_Rapid': args.connectorTypeRapid,
        'connector_type_Ultra-Rapid': args.connectorTypeUltraRapid,
        'connector_type_iCharging': args.connectorTypeICharging,
        'connector_avg_usage': args.connectorAvgUsage,
        'station_avg_usage': args.stationAvgUsage,
        'distance_to_center': args.distanceToCenter,
        'city_station_density': args.cityStationDensity,
        'station_connector_count': args.stationConnectorCount,
        'station_avg_max_chargerate': args.stationAvgMaxChargerate,
        'station_density_10km': args.stationDensity10km,
        'station_density_1km': args.stationDensity1km,
        'station_density_20km': args.stationDensity20km,
        'station_name_encoded': station_name_encoded,
        'city_id_encoded': city_id_encoded,
        'hour_sin': hour_sin,
        'hour_cos': hour_cos,
        'day_of_week_sin': day_of_week_sin,
        'day_of_week_cos': day_of_week_cos
    }

    weather_features = ['Clear', 'Clouds', 'Drizzle', 'Fog', 'Haze', 'Mist', 'Rain']
    for weather in weather_features:
        features_dict[f'weather_{weather}'] = 1 if args.weather == weather else 0

    # 创建有序字典，按照模型期望的特征顺序排列
    ordered_features = {name: features_dict.get(name, 0) for name in expected_feature_names}

    # 创建 DataFrame，确保列的顺序与模型期望的特征顺序一致
    features_df = pd.DataFrame([ordered_features], columns=expected_feature_names)

    # 进行预测并获取概率值
    probabilities = loaded_rf.predict_proba(features_df)
    
    # 获取类别标签
    class_labels = loaded_rf.classes_

    # 打印每个类别的概率
    print("\n Probability:")
    for label, prob in zip(class_labels, probabilities[0]):
        print(f"Class {label}: {prob:.4f}")

if __name__ == "__main__":
    main()