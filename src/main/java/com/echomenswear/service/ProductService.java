package com.echomenswear.service;

import com.echomenswear.dto.ProductRequest;
import com.echomenswear.model.Product;
import com.echomenswear.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByStore(String storeId) {
        return productRepository.findByStoreId(storeId);
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setSize(request.getSize());
        product.setColor(request.getColor());
        product.setCurrentQuantity(request.getQuantity());
        product.setBuyingPrice(request.getBuyingPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setStoreId(request.getStoreId());
        product.setSupplierId(request.getSupplierId());
        return productRepository.save(product);
    }

    public Product updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setSize(request.getSize());
        product.setColor(request.getColor());
        product.setBuyingPrice(request.getBuyingPrice());
        product.setSellingPrice(request.getSellingPrice());
        product.setSupplierId(request.getSupplierId());
        // quantity is not updated here; use stock-in/out endpoints
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    public Product addStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setCurrentQuantity(product.getCurrentQuantity() + quantity);
        return productRepository.save(product);
    }

    public Product removeStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (product.getCurrentQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getCurrentQuantity());
        }
        product.setCurrentQuantity(product.getCurrentQuantity() - quantity);
        return productRepository.save(product);
    }
}