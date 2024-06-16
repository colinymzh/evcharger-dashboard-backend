package com.evcharger.dashboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.evcharger.dashboard.mapper")
public class EvChargerUtilisationDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvChargerUtilisationDashboardApplication.class, args);
	}

}
