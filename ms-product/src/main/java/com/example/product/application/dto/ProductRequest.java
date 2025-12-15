package com.example.product.application.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(min = 3, max = 100) String name,
        @NotBlank @Size(min = 10, max = 500) String description,
        @NotNull @DecimalMin("0.01") @Digits(integer = 8, fraction = 2) BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotBlank String category,
        String imageUrl
) {}
