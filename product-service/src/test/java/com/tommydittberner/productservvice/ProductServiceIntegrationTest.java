package com.tommydittberner.productservvice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tommydittberner.productservvice.dto.ProductRequest;
import com.tommydittberner.productservvice.model.Product;
import com.tommydittberner.productservvice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceIntegrationTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@Container
	// using mongo:6.0 docker image for testing
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");


	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		// adding replicaset url to spring properties at the time of running the test
		// so the mongo db from the docker container can be accessed
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setup() {
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProduct() throws Exception {
		mvc.perform(post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(getProductRequest())))
				.andExpect(status().isCreated());

		assertEquals(1, productRepository.findAll().size());
	}

	@Test
	void shouldGetProducts() throws Exception {
		productRepository.save(Product.builder().id("000").build());

		mvc.perform(get("/api/product")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				// TODO: find more elegant way
				.andExpect(content().json("[{\"id\":\"000\",\"name\":null,\"description\":null,\"price\":null}]"));

	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Pixel 6a")
				.description("Googles latest and greatest")
				.price(BigDecimal.valueOf(400))
				.build();
	}


}
