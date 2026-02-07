package com.srv.setebit.dropshipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EntityScan(basePackages = "com.srv.setebit.dropshipping.infrastructure.persistence.jpa")
@EnableJpaRepositories(basePackages = "com.srv.setebit.dropshipping.infrastructure.persistence.jpa",
        entityManagerFactoryRef = "entityManagerFactory")
public class DropshippingApplication {

	public static void main(String[] args) {
		SpringApplication.run(DropshippingApplication.class, args);
	}

}
