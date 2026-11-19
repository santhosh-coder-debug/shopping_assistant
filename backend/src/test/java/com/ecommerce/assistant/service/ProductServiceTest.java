
package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.dto.ProductWriteRequest;
import com.ecommerce.assistant.entity.Product;
import com.ecommerce.assistant.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.mockito.ArgumentCaptor;

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
    private ApplicationEventPublisher eventPublisher;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        productService = new ProductService(repository, eventPublisher);
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
    void shouldCreateProductFromValidatedWriteModel() {
        ProductWriteRequest request = new ProductWriteRequest(
                "LAP-002", "Studio Laptop", "Northstar", "Laptop",
                "32GB RAM", new BigDecimal("1499.00"), "USD", 5, true);
        when(repository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ProductDto result = productService.create(request);

        assertEquals(2L, result.id());
        assertEquals("LAP-002", result.sku());
        assertEquals("Studio Laptop", result.name());
        assertEquals(new BigDecimal("1499.00"), result.price());
        ArgumentCaptor<ProductChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(ProductChangedEvent.class);

        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ProductChangedEvent event = eventCaptor.getValue();

        assertEquals(2L, event.product().id());
        assertEquals("Studio Laptop", event.product().name());
        assertTrue(event.product().active());
    }

    @Test
    void shouldUpdateInactiveProductsById() {
        Product inactive = new Product();
        inactive.setId(3L);
        inactive.setSku("LAP-003");
        inactive.setName("Inactive Laptop");
        inactive.setPrice(new BigDecimal("500.00"));
        inactive.setCurrency("USD");
        inactive.setStockQuantity(0);
        inactive.setActive(false);
        ProductWriteRequest request = new ProductWriteRequest(
                "LAP-003", "Restocked Laptop", "Northstar", "Laptop",
                "Updated description", new BigDecimal("600.00"), "USD", 4, true);
        when(repository.findById(3L)).thenReturn(Optional.of(inactive));
        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDto result = productService.update(3L, request);

        assertEquals(3L, result.id());
        assertEquals("Restocked Laptop", result.name());
        assertTrue(result.active());
        ArgumentCaptor<ProductChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(ProductChangedEvent.class);

        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ProductChangedEvent event = eventCaptor.getValue();

        assertEquals(3L, event.product().id());
        assertEquals("Restocked Laptop", event.product().name());
        assertEquals(4, event.product().stockQuantity());
        assertTrue(event.product().active());
    }
}
