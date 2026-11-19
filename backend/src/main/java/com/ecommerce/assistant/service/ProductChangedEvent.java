package com.ecommerce.assistant.service;

import com.ecommerce.assistant.dto.ProductDto;

public record ProductChangedEvent(ProductDto product) {
}
