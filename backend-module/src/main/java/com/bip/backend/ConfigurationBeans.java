package com.bip.backend;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bip.ejb.service.BeneficioEjbService;
import com.bip.ejb.service.TransferenciaEjbService;

@Configuration
public class ConfigurationBeans {

	@Bean
	BeneficioEjbService BeneficioEjbService() {
		return new BeneficioEjbService();
	}

	@Bean
	TransferenciaEjbService transferenciaEjbService() {
		return new TransferenciaEjbService();
	}

	@Bean
	FlywayMigrationStrategy flywayMigrationStrategy() {
		return flyway -> {
			flyway.repair();
			flyway.migrate();

		};
	}
}
