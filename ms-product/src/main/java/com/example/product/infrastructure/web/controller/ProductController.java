package com.example.product.infrastructure.web.controller;

import com.example.product.application.dto.*;
import com.example.product.application.mapper.ProductMapper;
import com.example.product.application.service.ProductService;
import com.example.product.domain.entity.Product;
import com.example.product.domain.entity.ProductCategory;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> all() {
        return service.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse one(@PathVariable Long id) {
        return ProductMapper.toResponse(service.findById(id));
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        Product p = ProductMapper.toEntity(request);
        return ProductMapper.toResponse(service.save(p));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProductRequest request) {
        Product p = service.findById(id);
        p.setName(request.name());
        p.setDescription(request.description());
        p.setPrice(request.price());
        p.setStock(request.stock());
        p.setCategory(ProductCategory.valueOf(request.category()));
        return ProductMapper.toResponse(service.save(p));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/stock")
    public void updateStock(@PathVariable Long id,
                            @RequestParam int stock) {
        service.updateStock(id, stock);
    }
}
