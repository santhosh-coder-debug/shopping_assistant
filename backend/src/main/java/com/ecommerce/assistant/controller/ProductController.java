package com.ecommerce.assistant.controller;
import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.entity.Product;
import com.ecommerce.assistant.service.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@RestController @RequestMapping("/api/v1/products")
public class ProductController {
 private final ProductService products;private final ProductSearchService search;private final ProductEmbeddingService embeddings;
 public ProductController(ProductService products,ProductSearchService search,ProductEmbeddingService embeddings){this.products=products;this.search=search;this.embeddings=embeddings;}
 @GetMapping public List<ProductDto> list(@RequestParam(required=false) String brand,@RequestParam(required=false) String category,@RequestParam(required=false) BigDecimal maxPrice,@RequestParam(required=false) String currency){return products.search(brand,category,maxPrice,currency);}
 @GetMapping("/{id}") public ProductDto get(@PathVariable long id){return products.get(id);}
 @GetMapping("/semantic") public List<ProductDto> semantic(@RequestParam String query){return search.semantic(query);}
 @PostMapping public ResponseEntity<ProductDto> create(@RequestBody Product product){product.setId(null);return ResponseEntity.status(HttpStatus.CREATED).body(products.save(product));}
 @PutMapping("/{id}") public ProductDto update(@PathVariable long id,@RequestBody Product input){products.get(id);input.setId(id);return products.save(input);}
 @PostMapping("/reindex") public java.util.Map<String,Integer> reindex(){return java.util.Map.of("indexed",embeddings.reindexAll());}
}
