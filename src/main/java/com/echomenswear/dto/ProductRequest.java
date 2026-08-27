package com.echomenswear.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private String sku;
    private String name;
    private String size;
    private String color;
    private int quantity;       // used for stock in/out
    private double buyingPrice;
    private double sellingPrice;
    private String storeId;
    private String supplierId;
}