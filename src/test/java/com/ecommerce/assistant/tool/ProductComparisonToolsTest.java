
package com.ecommerce.assistant.tool;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductComparisonToolsTest {

    private ProductService productService;
    private ProductComparisonTools comparisonTools;

    @BeforeEach
    void setUp() {
        productService = Mockito.mock(ProductService.class);
        comparisonTools = new ProductComparisonTools(productService);
    }

    @Test
    void shouldCalculateCorrectSavings() {

        ProductDto creator = new ProductDto(
                1L,
                "LAP-001",
                "Creator Laptop 14",
                "Northstar",
                "Laptop",
                "16GB RAM, 512GB SSD",
                new BigDecimal("899.00"),
                "USD",
                12,
                true
        );

        ProductDto studio = new ProductDto(
                2L,
                "LAP-002",
                "Studio Laptop 16",
                "Northstar",
                "Laptop",
                "32GB RAM, 1TB SSD",
                new BigDecimal("1499.00"),
                "USD",
                5,
                true
        );

        when(productService.findByName("Creator Laptop 14"))
                .thenReturn(creator);

        when(productService.findByName("Studio Laptop 16"))
                .thenReturn(studio);

        String result = comparisonTools.calculatePriceDifference(
                "Creator Laptop 14",
                "Studio Laptop 16"
        );

        assertEquals(
                "Creator Laptop 14 is cheaper by 600.00 USD.",
                result
        );
    }

    @Test
    void shouldCompareTwoProducts() {

        ProductDto creator = new ProductDto(
                1L, "LAP-001", "Creator Laptop 14",
                "Northstar", "Laptop",
                "16GB RAM, 512GB SSD",
                new BigDecimal("899.00"),
                "USD", 12, true
        );

        ProductDto studio = new ProductDto(
                2L, "LAP-002", "Studio Laptop 16",
                "Northstar", "Laptop",
                "32GB RAM, 1TB SSD",
                new BigDecimal("1499.00"),
                "USD", 5, true
        );

        when(productService.findByName("Creator Laptop 14"))
                .thenReturn(creator);

        when(productService.findByName("Studio Laptop 16"))
                .thenReturn(studio);

        List<ProductDto> result = comparisonTools.compareProducts(
                "Creator Laptop 14",
                "Studio Laptop 16"
        );

        assertEquals(2, result.size());
        assertEquals("Creator Laptop 14", result.get(0).name());
        assertEquals("Studio Laptop 16", result.get(1).name());
        assertEquals(
                new BigDecimal("899.00"),
                result.get(0).price()
        );
        assertEquals(
                new BigDecimal("1499.00"),
                result.get(1).price()
        );
    }
}
