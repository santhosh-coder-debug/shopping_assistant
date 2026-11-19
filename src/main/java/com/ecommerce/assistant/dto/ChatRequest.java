package com.ecommerce.assistant.dto;
import jakarta.validation.constraints.NotBlank;
public record ChatRequest(@NotBlank String conversationId,@NotBlank String message) {}
