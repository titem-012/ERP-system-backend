package com.echomenswear.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "transactions")
@Data
public class Transaction {
    @Id
    private String id;
    private String type; // from TransactionType enum
    private LocalDateTime date;
    private String storeId; // "main" or "sub"
    private List<Item> items;
    private double totalAmount;
    private String note;

    @Data
    public static class Item {
        private String productName;
        private int quantity;
        private double unitPrice;
        private String size;
        private String color;
    }
}