package com.example.product.application.mapper;

import com.example.product.application.dto.ProductRequest;
import com.example.product.application.dto.ProductResponse;
import com.example.product.domain.entity.Product;
import com.example.product.domain.entity.ProductCategory;

public class ProductMapper {

    public static Product toEntity(ProductRequest dto) {
        Product p = new Product();
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setCategory(ProductCategory.valueOf(dto.category()));
        p.setImageUrl(dto.imageUrl());
        return p;
    }

    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getCategory().name(),
                p.getActive(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
