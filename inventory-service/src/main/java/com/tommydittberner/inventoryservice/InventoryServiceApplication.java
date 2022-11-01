package com.tommydittberner.inventoryservice;

import com.tommydittberner.inventoryservice.model.Inventory;
import com.tommydittberner.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository repository) {
		return args -> {
			var item_000 = Inventory.builder()
					.skuCode("Pixel_6a")
					.quantity(2)
					.build();

			var item_001 = Inventory.builder()
					.skuCode("iPhone_14")
					.quantity(0)
					.build();

			repository.saveAll(List.of(item_000, item_001));
		};
	}
}
