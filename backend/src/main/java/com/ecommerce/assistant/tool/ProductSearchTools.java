
package com.ecommerce.assistant.tool;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.service.ProductService;
import com.ecommerce.assistant.service.ProductSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductSearchTools {

 private static final Logger log =
         LoggerFactory.getLogger(ProductSearchTools.class);

 private final ProductService products;
 private final ProductSearchService search;

 public ProductSearchTools(
         ProductService products,
         ProductSearchService search) {
  this.products = products;
  this.search = search;
 }


 @Tool(description = """
    Search the product catalog using structured filters.
    Use this tool when the customer specifies a category,
    brand, maximum price, or currency.

    Parameter rules:
    - brand: brand name only, or null.
    - category: product category such as Laptop or Phone, or null.
    - maxPrice: numeric amount only, such as 1000 or 899.99.
      Never pass product names, category names, currency
      symbols, or text into maxPrice.
    - currency: ISO currency code such as USD, or null.

    Do not invent product information.
    """)
 public List<ProductDto> searchProducts(
         @ToolParam(
                 description = "Brand name only, or null",
                 required = false
         )
         String brand,

         @ToolParam(
                 description = "Product category, e.g. Laptop or Phone, or null",
                 required = false
         )
         String category,

         @ToolParam(
                 description = "Numeric maximum price only, e.g. 1000 or 899.99. No text or currency symbols.",
                 required = false
         )
         BigDecimal maxPrice,

         @ToolParam(
                 description = "Currency code such as USD, or null",
                 required = false
         )
         String currency) {

  log.info(
          "AI TOOL CALLED: searchProducts brand={}, category={}, maxPrice={}, currency={}",
          brand, category, maxPrice, currency
  );

  List<ProductDto> results =
          products.search(brand, category, maxPrice, currency);

  log.info("searchProducts returned {} products", results.size());

  return results;
 }


 @Tool(description = "Find products matching a customer's use case or descriptive preferences by semantic similarity. Prices and stock are refreshed from the catalog.")
 public List<ProductDto> semanticProductSearch(
         @ToolParam(description = "Natural language product requirements") String query) {

  log.info("AI TOOL CALLED: semanticProductSearch query={}", query);

  List<ProductDto> results = search.semantic(query);

  log.info("semanticProductSearch returned {} products", results.size());

  return results;
 }

 @Tool(description = "Get current catalog details for a product ID")
 public ProductDto getProductDetails(long productId) {

  log.info("AI TOOL CALLED: getProductDetails productId={}", productId);

  ProductDto product = products.get(productId);

  log.info("getProductDetails returned productId={}", product.id());

  return product;
 }
}
