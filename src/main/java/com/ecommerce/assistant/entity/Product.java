package com.ecommerce.assistant.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="products")
public class Product {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String sku;
 @Column(nullable=false) private String name;
 private String brand; private String category;
 @Column(columnDefinition="text") private String description;
 @Column(nullable=false,precision=12,scale=2) private BigDecimal price;
 @Column(nullable=false,length=3) private String currency="USD";
 @Column(nullable=false) private Integer stockQuantity=0;
 @Column(nullable=false) private boolean active=true;
 public Long getId(){return id;} public void setId(Long v){id=v;}
 public String getSku(){return sku;} public void setSku(String v){sku=v;}
 public String getName(){return name;} public void setName(String v){name=v;}
 public String getBrand(){return brand;} public void setBrand(String v){brand=v;}
 public String getCategory(){return category;} public void setCategory(String v){category=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal v){price=v;}
 public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
 public Integer getStockQuantity(){return stockQuantity;} public void setStockQuantity(Integer v){stockQuantity=v;}
 public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
