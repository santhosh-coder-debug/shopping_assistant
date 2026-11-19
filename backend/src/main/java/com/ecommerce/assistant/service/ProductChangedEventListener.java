package com.ecommerce.assistant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProductChangedEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(ProductChangedEventListener.class);

    private final ProductEmbeddingService embeddings;

    public ProductChangedEventListener(ProductEmbeddingService embeddings) {
        this.embeddings = embeddings;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductChanged(ProductChangedEvent event) {
        try {
            embeddings.indexProduct(event.product());
        } catch (Exception ex) {
            log.error(
                    "Failed to index product {} after database commit",
                    event.product().id(),
                    ex
            );
        }
    }
}