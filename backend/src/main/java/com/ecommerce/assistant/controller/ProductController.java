package com.ecommerce.assistant.controller;
import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.dto.ProductWriteRequest;
import com.ecommerce.assistant.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
 private final ProductService products;
 private final ProductSearchService search;
 private final ProductEmbeddingService embeddings;

 public ProductController(
         ProductService products,
         ProductSearchService search,
         ProductEmbeddingService embeddings) {
  this.products = products;
  this.search = search;
  this.embeddings = embeddings;
 }

 @GetMapping
 public List<ProductDto> list(
         @RequestParam(required = false) @Size(max = 255) String brand,
         @RequestParam(required = false) @Size(max = 255) String category,
         @RequestParam(required = false) @DecimalMin("0.00") BigDecimal maxPrice,
         @RequestParam(required = false) @Pattern(regexp = "[A-Za-z]{3}") String currency) {
  return products.search(brand, category, maxPrice, currency);
 }

 @GetMapping("/{id}")
 public ProductDto get(@PathVariable long id) {
  return products.get(id);
 }

 @GetMapping("/semantic")
 public List<ProductDto> semantic(
         @RequestParam @NotBlank @Size(max = 500) String query) {
  return search.semantic(query);
 }

 @PostMapping
 public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductWriteRequest request) {
  return ResponseEntity.status(HttpStatus.CREATED).body(products.create(request));
 }

 @PutMapping("/{id}")
 public ProductDto update(
         @PathVariable long id,
         @Valid @RequestBody ProductWriteRequest request) {
  return products.update(id, request);
 }

 @PostMapping("/reindex")
 public java.util.Map<String, Integer> reindex() {
  return java.util.Map.of("indexed", embeddings.reindexAll());
 }
}
