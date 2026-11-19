
package com.ecommerce.assistant.service;

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

  for (var p : all) {

   String id = UUID.nameUUIDFromBytes(
           ("product-" + p.id())
                   .getBytes(StandardCharsets.UTF_8)
   ).toString();

   String content = p.name()
           + ". Brand: " + p.brand()
           + ". Category: " + p.category()
           + ". " + p.description();

   store.delete(List.of(id));

   Document document = new Document(
           id,
           content,
           Map.of(
                   "productId", p.id(),
                   "currency", p.currency()
           )
   );

   store.add(List.of(document));
  }

  return all.size();
 }
}
