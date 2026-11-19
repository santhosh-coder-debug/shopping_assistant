
package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProductSearchService {

 private final VectorStore vectorStore;
 private final ProductService products;

 public ProductSearchService(VectorStore vectorStore, ProductService products) {
  this.vectorStore = vectorStore;
  this.products = products;
 }

 public List<ProductDto> semantic(String query) {
  if (query == null || query.isBlank()) {
   return List.of();
  }

  List<Long> ids = vectorStore.similaritySearch(
                  SearchRequest.builder()
                          .query(query.trim())
                          .topK(12)
                          .build())
          .stream()
          .map(document -> document.getMetadata().get("productId"))
          .map(this::parseProductId)
          .filter(Objects::nonNull)
          .distinct()
          .toList();

  return products.findActiveByIds(ids);
 }

 private Long parseProductId(Object value) {
  if (value == null) {
   return null;
  }

  try {
   long id = Long.parseLong(value.toString().trim());
   return id > 0 ? id : null;
  } catch (NumberFormatException ex) {
   return null;
  }
 }
}
