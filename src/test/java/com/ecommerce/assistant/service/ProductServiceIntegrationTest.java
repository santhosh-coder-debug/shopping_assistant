
package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.entity.Product;
import com.ecommerce.assistant.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@ActiveProfiles("integrationtest")
@Import(ProductService.class)
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {

        // Safety check: only execute against the test database.
        // See additional database guard in Step 2.

        repository.saveAllAndFlush(List.of(
                createProduct(
                        "LAP-001", "Creator Laptop 14",
                        "Laptop", "899.00", 12, true
                ),
                createProduct(
                        "LAP-002", "Studio Laptop 16",
                        "Laptop", "1499.00", 5, true
                ),
                createProduct(
                        "PHN-001", "Camera Phone Pro",
                        "Phone", "749.00", 20, true
                ),
                createProduct(
                        "AUD-001", "Wireless Headphones",
                        "Audio", "179.00", 35, true
                ),
                createProduct(
                        "LAP-003", "Inactive Laptop",
                        "Laptop", "599.00", 10, false
                ),
                createProduct(
                        "LAP-004", "Out of Stock Laptop",
                        "Laptop", "699.00", 0, true
                )
        ));
    }

    private Product createProduct(
            String sku,
            String name,
            String category,
            String price,
            int stock,
            boolean active
    ) {
        Product product = new Product();

        product.setSku(sku);
        product.setName(name);
        product.setBrand("Northstar");
        product.setCategory(category);
        product.setDescription("Integration test product");
        product.setPrice(new BigDecimal(price));
        product.setCurrency("USD");
        product.setStockQuantity(stock);
        product.setActive(active);

        return product;
    }

    @Test
    void shouldFilterByCategory() {

        List<ProductDto> result = productService.search(
                null, "Laptop", null, null
        );

        assertEquals(2, result.size());

        assertTrue(result.stream()
                .allMatch(p -> p.category().equals("Laptop")));
    }

    @Test
    void shouldFilterByMaximumPrice() {

        List<ProductDto> result = productService.search(
                null,
                "Laptop",
                new BigDecimal("1000.00"),
                "USD"
        );

        assertEquals(1, result.size());

        assertEquals(
                "Creator Laptop 14",
                result.get(0).name()
        );

        assertEquals(
                0,
                new BigDecimal("899.00")
                        .compareTo(result.get(0).price())
        );
    }

    @Test
    void shouldExcludeInactiveAndOutOfStockProducts() {

        List<ProductDto> result = productService.search(
                null, "Laptop", null, null
        );

        assertEquals(2, result.size());

        assertFalse(result.stream()
                .anyMatch(p ->
                        p.name().equals("Inactive Laptop")));

        assertFalse(result.stream()
                .anyMatch(p ->
                        p.name().equals("Out of Stock Laptop")));
    }

    @Test
    void shouldIgnoreLiteralNullFilters() {

        List<ProductDto> result = productService.search(
                "null", "null", null, "null"
        );

        assertEquals(4, result.size());
    }
}
