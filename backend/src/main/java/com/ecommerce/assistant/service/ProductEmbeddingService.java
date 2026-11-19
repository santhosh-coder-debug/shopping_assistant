
package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductEmbeddingService {

 private final VectorStore store;
 private final ProductService products;

 public ProductEmbeddingService(
         VectorStore store,
         ProductService products) {
  this.store = store;
  this.products = products;
 }

 public int reindexAll() {
  var all = products.all();

  for (var product : all) {
   indexProduct(product);
  }

  return all.size();
 }

 public void indexProduct(ProductDto product) {
  String documentId = documentId(product.id());

  // Remove the previous embedding, if present.
  store.delete(List.of(documentId));

  // Do not index inactive or out-of-stock products.
  if (!product.active() || product.stockQuantity() <= 0) {
   return;
  }

  String content = product.name()
          + ". Brand: " + product.brand()
          + ". Category: " + product.category()
          + ". " + product.description();

  Document document = new Document(
          documentId,
          content,
          Map.of(
                  "productId", product.id(),
                  "currency", product.currency()
          )
  );

  store.add(List.of(document));
 }

 private String documentId(Long productId) {
  return UUID.nameUUIDFromBytes(
          ("product-" + productId)
                  .getBytes(StandardCharsets.UTF_8)
  ).toString();
 }
}
