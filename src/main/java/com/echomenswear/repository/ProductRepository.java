package com.echomenswear.repository;

import com.echomenswear.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByStoreId(String storeId);
    Optional<Product> findByNameAndStoreId(String name, String storeId);
    Optional<Product> findBySkuAndStoreId(String sku, String storeId);
}