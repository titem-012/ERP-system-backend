package com.echomenswear.service;

import com.echomenswear.dto.TransactionRequest;
import com.echomenswear.model.Transaction;
import com.echomenswear.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ProductService productService;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByStore(String storeId) {
        return transactionRepository.findByStoreId(storeId);
    }

    public Transaction createTransaction(TransactionRequest request) {
        // Validate that all items exist in stock for stock-out types
        String type = request.getType();
        boolean isOut = List.of("SALE", "DAMAGE", "RETURN_TO_SUPPLIER", "TRANSFER_OUT").contains(type);

        if (isOut) {
            for (Transaction.Item item : request.getItems()) {
                // For stock-out, we need to check product existence and quantity
                // We'll use product name + storeId to find the product
                // Alternatively, we could require productId; but our frontend sends productName.
                // We'll query by name and storeId (case-insensitive)
                List<com.echomenswear.model.Product> products = productService.getProductsByStore(request.getStoreId())
                        .stream()
                        .filter(p -> p.getName().equalsIgnoreCase(item.getProductName()))
                        .toList();

                if (products.isEmpty()) {
                    throw new RuntimeException("Product '" + item.getProductName() + "' not found in this store.");
                }

                com.echomenswear.model.Product product = products.get(0);
                if (product.getCurrentQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product '" + item.getProductName() +
                            "'. Available: " + product.getCurrentQuantity());
                }

                // Deduct stock
                productService.removeStock(product.getId(), item.getQuantity());
            }
        } else {
            // Stock-in: add stock to products
            for (Transaction.Item item : request.getItems()) {
                // Find product by name and store, or create new one if not exists
                List<com.echomenswear.model.Product> products = productService.getProductsByStore(request.getStoreId())
                        .stream()
                        .filter(p -> p.getName().equalsIgnoreCase(item.getProductName()))
                        .toList();

                com.echomenswear.model.Product product;
                if (products.isEmpty()) {
                    // Create new product with default values (or you can pass more data)
                    product = new com.echomenswear.model.Product();
                    product.setName(item.getProductName());
                    product.setSize(item.getSize());
                    product.setColor(item.getColor());
                    product.setStoreId(request.getStoreId());
                    product.setCurrentQuantity(0);
                    product.setBuyingPrice(item.getUnitPrice());
                    product.setSellingPrice(item.getUnitPrice() * 1.3); // placeholder
                    product.setSku("AUTO-" + System.currentTimeMillis());
                    product = productService.createProduct(
                        new com.echomenswear.dto.ProductRequest(
                            product.getSku(), product.getName(), product.getSize(), product.getColor(),
                            product.getCurrentQuantity(), product.getBuyingPrice(), product.getSellingPrice(),
                            product.getStoreId(), null
                        )
                    );
                } else {
                    product = products.get(0);
                }

                // Add stock
                productService.addStock(product.getId(), item.getQuantity());
            }
        }

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setType(request.getType());
        transaction.setDate(LocalDateTime.now());
        transaction.setStoreId(request.getStoreId());
        transaction.setItems(request.getItems());
        transaction.setTotalAmount(request.getTotalAmount());
        transaction.setNote(request.getNote());

        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(String id, TransactionRequest request) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        existing.setType(request.getType());
        existing.setStoreId(request.getStoreId());
        existing.setItems(request.getItems());
        existing.setTotalAmount(request.getTotalAmount());
        existing.setNote(request.getNote());
        return transactionRepository.save(existing);
    }

    public void deleteTransaction(String id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }
}