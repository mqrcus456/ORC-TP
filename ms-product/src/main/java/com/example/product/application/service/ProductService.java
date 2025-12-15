package com.example.product.application.service;

import com.example.product.domain.entity.Product;
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

    public void delete(Long id) {
        // règle métier : ici on vérifierait les commandes
        repository.deleteById(id);
    }

    public void updateStock(Long id, int stock) {
        if (stock < 0) throw new IllegalArgumentException("Stock négatif interdit");
        Product p = findById(id);
        p.setStock(stock);
    }
}
