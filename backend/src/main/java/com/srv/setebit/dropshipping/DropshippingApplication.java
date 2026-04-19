package com.srv.setebit.dropshipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.srv.setebit.dropshipping.infrastructure.mercadolivre.MercadoLivreProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties(MercadoLivreProperties.class)
@EnableFeignClients(basePackages = "com.srv.setebit.dropshipping.infrastructure.client")
@EnableJpaRepositories(basePackages = "com.srv.setebit.dropshipping.infrastructure.persistence.jpa", entityManagerFactoryRef = "entityManagerFactory")
public class DropshippingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DropshippingApplication.class, args);
	}

}
