package com.ecommerce.assistant.config;

import com.ecommerce.assistant.controller.ProductController;
import com.ecommerce.assistant.service.ProductEmbeddingService;
import com.ecommerce.assistant.service.ProductSearchService;
import com.ecommerce.assistant.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.admin-api-key=test-admin-key")
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService products;

    @MockBean
    private ProductSearchService search;

    @MockBean
    private ProductEmbeddingService embeddings;

    @Test
    void allowsPublicCatalogReads() throws Exception {
        when(products.search(null, null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsProductWritesWithoutAdminKey() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requiresAdminKeyBeforeValidatingProductWrite() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .header("X-Admin-Key", "test-admin-key")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deniesUnconfiguredRoutes() throws Exception {
        mockMvc.perform(get("/api/v1/private"))
                .andExpect(status().isForbidden());
    }
}
