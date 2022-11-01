package com.tommydittberner.productservvice.repository;

import com.tommydittberner.productservvice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
