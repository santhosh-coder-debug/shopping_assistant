package com.ecommerce.assistant.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank
        @Size(max = 128)
        @Pattern(regexp = "[A-Za-z0-9._-]+")
        String conversationId,
        @NotBlank @Size(max = 2000) String message) {
}
