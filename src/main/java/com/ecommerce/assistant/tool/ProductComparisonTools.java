
package com.ecommerce.assistant.tool;

import com.ecommerce.assistant.dto.ProductDto;
import com.ecommerce.assistant.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductComparisonTools {

 private static final Logger log =
         LoggerFactory.getLogger(ProductComparisonTools.class);

 private final ProductService products;

 public ProductComparisonTools(ProductService products) {
  this.products = products;
 }

 @Tool(description = """
            Compare two products by their exact catalog names.
            Use this tool when customers ask to compare product
            specifications, prices, RAM, storage, or features.
            Never invent product IDs or specifications.
            """)
 public List<ProductDto> compareProducts(
         @ToolParam(description = "Exact first product name")
         String firstProductName,

         @ToolParam(description = "Exact second product name")
         String secondProductName) {

  log.info(
          "AI TOOL CALLED: compareProducts first={}, second={}",
          firstProductName,
          secondProductName
  );

  ProductDto first = products.findByName(firstProductName);
  ProductDto second = products.findByName(secondProductName);

  List<ProductDto> result = List.of(first, second);

  log.info("compareProducts returned {} products", result.size());

  return result;
 }

 @Tool(description = """
            Calculate the exact price difference between two
            products using their catalog names.
            Use when customers ask which product is cheaper,
            how much money they save, or the price difference.
            Never estimate the price difference.
            """)
 public String calculatePriceDifference(
         @ToolParam(description = "Exact first product name")
         String firstProductName,

         @ToolParam(description = "Exact second product name")
         String secondProductName) {

  log.info(
          "AI TOOL CALLED: calculatePriceDifference first={}, second={}",
          firstProductName,
          secondProductName
  );

  ProductDto first = products.findByName(firstProductName);
  ProductDto second = products.findByName(secondProductName);

  if (!first.currency().equalsIgnoreCase(second.currency())) {
   return "Cannot compare prices in different currencies.";
  }

  BigDecimal difference =
          first.price().subtract(second.price()).abs();

  if (difference.compareTo(BigDecimal.ZERO) == 0) {
   return String.format(
           "%s and %s have the same price: %s %s.",
           first.name(),
           second.name(),
           first.price().toPlainString(),
           first.currency()
   );
  }

  ProductDto cheaper =
          first.price().compareTo(second.price()) < 0
                  ? first
                  : second;

  String result = String.format(
          "%s is cheaper by %s %s.",
          cheaper.name(),
          difference.toPlainString(),
          first.currency()
  );

  log.info("Price comparison result: {}", result);

  return result;
 }
}
