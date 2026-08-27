package com.echomenswear.model.enums;

public enum TransactionType {
    PURCHASE,         // stock in
    RETURN_IN,        // stock in (return)
    TRANSFER_IN,      // stock in (from other store)
    SALE,             // stock out
    DAMAGE,           // stock out
    RETURN_TO_SUPPLIER, // stock out
    TRANSFER_OUT      // stock out (to other store)
}