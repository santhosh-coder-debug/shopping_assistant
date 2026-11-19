
package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.entity.Product;
import com.ecommerce.assistant.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository repository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        productService = new ProductService(repository);
    }

    private Product createProduct(
            Long id,
            String name,
            BigDecimal price
    ) {
        Product product = mock(Product.class);

        when(product.getId()).thenReturn(id);
        when(product.getSku()).thenReturn("LAP-001");
        when(product.getName()).thenReturn(name);
        when(product.getBrand()).thenReturn("Northstar");
        when(product.getCategory()).thenReturn("Laptop");
        when(product.getDescription())
                .thenReturn("16GB RAM, 512GB SSD");
        when(product.getPrice()).thenReturn(price);
        when(product.getCurrency()).thenReturn("USD");
        when(product.getStockQuantity()).thenReturn(12);
        when(product.isActive()).thenReturn(true);

        return product;
    }

    @Test
    void shouldSearchProducts() {

        Product creator = createProduct(
                1L,
                "Creator Laptop 14",
                new BigDecimal("899.00")
        );

        Page<Product> page =
                new PageImpl<>(List.of(creator));

        when(repository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);

        List<ProductDto> result = productService.search(
                "Northstar",
                "Laptop",
                new BigDecimal("1000.00"),
                "USD"
        );

        assertEquals(1, result.size());
        assertEquals("Creator Laptop 14", result.get(0).name());
        assertEquals(
                new BigDecimal("899.00"),
                result.get(0).price()
        );

        verify(repository).findAll(
                any(Specification.class),
                argThat((Pageable pageable) ->
                        pageable.getPageNumber() == 0
                                && pageable.getPageSize() == 20
                                && pageable.getSort()
                                .getOrderFor("price") != null
                                && pageable.getSort()
                                .getOrderFor("price").isAscending()
                )
        );
    }

    @Test
    void shouldHandleNullStringFilters() {

        when(repository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(Page.empty());

        List<ProductDto> result = productService.search(
                "null",
                "null",
                null,
                "null"
        );

        assertTrue(result.isEmpty());

        verify(repository).findAll(
                any(Specification.class),
                any(Pageable.class)
        );
    }

    @Test
    void shouldFindProductByName() {

        Product creator = createProduct(
                1L,
                "Creator Laptop 14",
                new BigDecimal("899.00")
        );

        when(repository.findByNameIgnoreCaseAndActiveTrue(
                "Creator Laptop 14"
        )).thenReturn(List.of(creator));

        ProductDto result = productService.findByName(
                " Creator Laptop 14 "
        );

        assertEquals("Creator Laptop 14", result.name());
        assertEquals(
                new BigDecimal("899.00"),
                result.price()
        );
    }

    @Test
    void shouldThrowWhenProductNotFoundByName() {

        when(repository.findByNameIgnoreCaseAndActiveTrue(
                "Unknown Laptop"
        )).thenReturn(List.of());

        assertThrows(
                NoSuchElementException.class,
                () -> productService.findByName("Unknown Laptop")
        );
    }

    @Test
    void shouldRejectBlankProductName() {

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.findByName("  ")
        );

        verifyNoInteractions(repository);
    }

    @Test
    void shouldGetProductById() {

        Product creator = createProduct(
                1L,
                "Creator Laptop 14",
                new BigDecimal("899.00")
        );

        when(repository.findById(1L))
                .thenReturn(Optional.of(creator));

        ProductDto result = productService.get(1L);

        assertEquals(1L, result.id());
        assertEquals("Creator Laptop 14", result.name());
    }

    @Test
    void shouldThrowWhenProductIdNotFound() {

        when(repository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> productService.get(99L)
        );
    }

    @Test
    void shouldReturnEmptyListForEmptyIds() {

        List<ProductDto> result =
                productService.findActiveByIds(List.of());

        assertTrue(result.isEmpty());
        verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNegativePrice() {

        Product product = mock(Product.class);

        when(product.getPrice())
                .thenReturn(new BigDecimal("-10.00"));

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.save(product)
        );

        verify(repository, never()).save(any(Product.class));
    }

    @Test
    void shouldRejectNegativeStock() {

        Product product = mock(Product.class);

        when(product.getPrice())
                .thenReturn(new BigDecimal("100.00"));
        when(product.getStockQuantity()).thenReturn(-1);

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.save(product)
        );

        verify(repository, never()).save(any(Product.class));
    }
}
