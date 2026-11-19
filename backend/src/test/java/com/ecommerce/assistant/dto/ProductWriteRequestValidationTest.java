package com.ecommerce.assistant.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductWriteRequestValidationTest {
    private static Validator validator;
    private static jakarta.validation.ValidatorFactory validatorFactory;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void acceptsValidProduct() {
        ProductWriteRequest request = new ProductWriteRequest(
                "SKU-1", "Product", "Brand", "Category", "Description",
                new BigDecimal("10.00"), "USD", 1, true);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsNegativePriceInvalidCurrencyAndNegativeStock() {
        ProductWriteRequest request = new ProductWriteRequest(
                "", "", null, null, null,
                new BigDecimal("-1.00"), "usd", -1, null);

        assertFalse(validator.validate(request).isEmpty());
    }
}
