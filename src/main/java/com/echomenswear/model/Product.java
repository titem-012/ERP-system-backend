package com.echomenswear.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
public class Product {
    @Id
    private String id;
    private String sku;
    private String name;
    private String size;
    private String color;
    private int currentQuantity;
    private double buyingPrice;
    private double sellingPrice;
    private String storeId; // "main" or "sub"
    private String supplierId;
}