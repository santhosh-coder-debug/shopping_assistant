package com.ecommerce.assistant.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductWriteRequest(
        @NotBlank @Size(max = 255) String sku,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String brand,
        @Size(max = 255) String category,
        @Size(max = 4000) String description,
        @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal price,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter uppercase currency code")
        String currency,
        @NotNull @PositiveOrZero Integer stockQuantity,
        @NotNull Boolean active) {
}
