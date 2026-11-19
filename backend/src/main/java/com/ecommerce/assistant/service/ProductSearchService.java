package com.ecommerce.assistant.service;
import com.ecommerce.assistant.dto.ProductDto;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class ProductSearchService {
 private final VectorStore vectorStore; private final ProductService products;
 public ProductSearchService(VectorStore vectorStore,ProductService products){this.vectorStore=vectorStore;this.products=products;}
 public List<ProductDto> semantic(String query){if(query==null||query.isBlank())return List.of();var ids=vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(12).build()).stream().map(d->d.getMetadata().get("productId")).filter(Objects::nonNull).map(Object::toString).map(Long::parseLong).toList();return products.findActiveByIds(ids);}
}
