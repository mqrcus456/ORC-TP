package com.example.product.domain.repository;

import com.example.product.domain.entity.Product;
import com.example.product.domain.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByStockGreaterThan(Integer stock);

    long countByStockLessThan(Integer stock);

    long countByCategory(ProductCategory category);
}
