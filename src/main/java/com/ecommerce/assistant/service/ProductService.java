
package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.entity.Product;
import com.ecommerce.assistant.repository.ProductRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {

 private final ProductRepository repository;

 public ProductService(ProductRepository repository) {
  this.repository = repository;
 }

 @Transactional(readOnly = true)
 public List<ProductDto> search(
         String brand,
         String category,
         BigDecimal maxPrice,
         String currency) {

  brand = normalizeOptional(brand);
  category = normalizeOptional(category);
  currency = normalizeOptional(currency);

  Specification<Product> spec =
          (root, query, cb) -> cb.and(
                  cb.isTrue(root.get("active")),
                  cb.greaterThan(root.get("stockQuantity"), 0)
          );


  if (brand != null) {

   String value = brand.trim().toLowerCase(Locale.ROOT);

   spec = spec.and((root, query, cb) ->
           cb.equal(cb.lower(root.get("brand")), value));
  }


  if (category != null && !category.isBlank()) {
   String value = category.trim().toLowerCase(Locale.ROOT);
   spec = spec.and((root, query, cb) ->
           cb.equal(cb.lower(root.get("category")), value));
  }

  if (maxPrice != null) {
   spec = spec.and((root, query, cb) ->
           cb.lessThanOrEqualTo(
                   root.<BigDecimal>get("price"),
                   maxPrice
           ));
  }

  if (currency != null && !currency.isBlank()) {
   String value = currency.trim().toUpperCase(Locale.ROOT);
   spec = spec.and((root, query, cb) ->
           cb.equal(root.get("currency"), value));
  }

  return repository.findAll(
                  spec,
                  PageRequest.of(0, 20, Sort.by("price").ascending())
          )
          .getContent()
          .stream()
          .map(ProductDto::from)
          .toList();
 }

 @Transactional(readOnly = true)
 public ProductDto get(long id) {
  return ProductDto.from(
          repository.findById(id)
                  .filter(Product::isActive)
                  .orElseThrow(() ->
                          new NoSuchElementException(
                                  "Product not found: " + id))
  );
 }

 @Transactional(readOnly = true)
 public ProductDto findByName(String name) {

  if (name == null || name.isBlank()) {
   throw new IllegalArgumentException(
           "Product name is required");
  }

  List<Product> matches = repository
          .findByNameIgnoreCaseAndActiveTrue(name.trim());

  if (matches.isEmpty()) {
   throw new NoSuchElementException(
           "Product not found: " + name);
  }

  if (matches.size() > 1) {
   throw new IllegalArgumentException(
           "Multiple products match: " + name);
  }

  return ProductDto.from(matches.get(0));
 }

 @Transactional(readOnly = true)
 public List<ProductDto> findActiveByIds(Collection<Long> ids) {
  if (ids.isEmpty()) {
   return List.of();
  }

  Map<Long, ProductDto> map = new HashMap<>();

  repository.findByIdInAndActiveTrue(ids)
          .forEach(p ->
                  map.put(p.getId(), ProductDto.from(p)));

  return ids.stream()
          .distinct()
          .map(map::get)
          .filter(Objects::nonNull)
          .toList();
 }

 @Transactional
 public ProductDto save(Product product) {
  if (product.getPrice() == null
          || product.getPrice().signum() < 0
          || product.getStockQuantity() == null
          || product.getStockQuantity() < 0) {
   throw new IllegalArgumentException(
           "Invalid price or stock");
  }

  return ProductDto.from(repository.save(product));
 }

 @Transactional(readOnly = true)
 public List<ProductDto> all() {
  return repository.findByActiveTrue()
          .stream()
          .map(ProductDto::from)
          .toList();
 }

 private String normalizeOptional(String value) {
  if (value == null || value.isBlank()) {
   return null;
  }

  String trimmed = value.trim();

  if (trimmed.equalsIgnoreCase("null")) {
   return null;
  }

  return trimmed;
 }
}
