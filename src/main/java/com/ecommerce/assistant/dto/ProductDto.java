package com.ecommerce.assistant.dto;
import com.ecommerce.assistant.entity.Product;
import java.math.BigDecimal;
public record ProductDto(Long id,String sku,String name,String brand,String category,String description,BigDecimal price,String currency,Integer stockQuantity,boolean active) {
 public static ProductDto from(Product p){return new ProductDto(p.getId(),p.getSku(),p.getName(),p.getBrand(),p.getCategory(),p.getDescription(),p.getPrice(),p.getCurrency(),p.getStockQuantity(),p.isActive());}
}
