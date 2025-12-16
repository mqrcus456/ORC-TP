package com.example.product.application.service;

import com.example.product.domain.entity.Product;
import com.example.product.domain.entity.ProductCategory;
import com.example.product.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product save(Product product) {
        return repository.save(product);
    }
    public List<Product> findAvailable() {
    return repository.findByStockGreaterThan(0);
    }

    public void delete(Long id) {
        // règle métier : ici on vérifierait les commandes
        repository.deleteById(id);
    }

    public void updateStock(Long id, int quantityToDecrease) {
        Product p = findById(id);
        int newStock = p.getStock() - quantityToDecrease;
        if (newStock < 0) throw new IllegalArgumentException("Stock insuffisant");
        p.setStock(newStock);
    }
    public List<Product> searchByName(String name) {
    return repository.findByNameContainingIgnoreCase(name);
    }
    public List<Product> findByCategory(ProductCategory category) {
    return repository.findByCategory(category);
    }

}
