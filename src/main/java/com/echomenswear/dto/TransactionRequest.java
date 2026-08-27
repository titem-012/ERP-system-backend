package com.echomenswear.dto;

import com.echomenswear.model.Transaction;
import lombok.Data;

import java.util.List;

@Data
public class TransactionRequest {
    private String type;
    private String storeId;
    private List<Transaction.Item> items;
    private double totalAmount;
    private String note;
}