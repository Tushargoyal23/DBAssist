package com.tushar.dbassist.dbassist.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeminiServiceTest {

    @Test
    void shouldReturnConfiguredModelAndFallbacks() {
        GeminiService service = new GeminiService();

        List<String> models = service.getModelsToTry("gemini-1.5-flash");

        assertEquals(List.of("gemini-1.5-flash", "gemini-1.5-pro", "gemini-2.0-flash-exp"), models);
    }
}
