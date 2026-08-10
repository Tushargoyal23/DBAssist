package com.tushar.dbassist.dbassist.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.tushar.dbassist.dbassist.model.QueryRequest;
import com.tushar.dbassist.dbassist.model.QueryResponse;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String configuredModel;

    @Autowired
    private ConnectionContextStore connectionContextStore;

    private final WebClient webClient = WebClient.create("https://generativelanguage.googleapis.com");

    public QueryResponse processQuery(QueryRequest request) {
        QueryResponse response = new QueryResponse();
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("Gemini API key is not configured. Set gemini.api.key or the GEMINI_API_KEY environment variable.");
            }

            String schemaText = request.getSchemaSummary();

            if ((schemaText == null || schemaText.isBlank()) && request.getQuestion() != null && !request.getQuestion().isBlank()) {
                schemaText = connectionContextStore.getLatestSchemaSummary();
            }
            response.setSchemaSummary(schemaText);

            String prompt = buildPrompt(schemaText, request.getQuestion());

            Map<String, Object> body = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            Exception lastError = null;
            for (String model : getModelsToTry(configuredModel)) {
                try {
                    Map<?, ?> result = webClient.post()
                            .uri("/v1beta/models/gemini-flash-latest:generateContent?key={key}", apiKey)
                            .header("Content-Type", "application/json")
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

                    String generatedText = extractText(result);
                    response.setSql(generatedText);
                    response.setExplanation("Query generated successfully");
                    response.setSuccess(true);
                    return response;
                } catch (WebClientResponseException e) {
                    lastError = e;
                    if (!isRetryable(e.getStatusCode().value())) {
                        break;
                    }
                } catch (Exception e) {
                    lastError = e;
                }
            }

            throw lastError != null ? lastError : new IllegalStateException("Unable to generate a SQL query.");
        } catch (Exception e) {
            response.setSuccess(false);
            response.setError("Failed to generate query: " + e.getMessage());
        }
        return response;
    }

    List<String> getModelsToTry(String configuredModel) {
        List<String> models = new ArrayList<>();
        String primaryModel = (configuredModel == null || configuredModel.isBlank()) ? "gemini-1.5-flash" : configuredModel;

        if (!"gemini-1.5-flash".equals(primaryModel)) {
            models.add(primaryModel);
        }
        models.addAll(List.of("gemini-1.5-flash", "gemini-1.5-pro", "gemini-2.0-flash-exp"));

        return models.stream().distinct().toList();
    }

    private boolean isRetryable(int statusCode) {
        return statusCode == 429 || statusCode == 500 || statusCode == 503;
    }

    private String buildPrompt(String schema, String question) {
        return """
            You are a SQL expert. Given the database schema summary below, answer the question asked.
            
            Schema summary:
            %s
            
            Question:
            %s
            
            Check if the user is asking for a query or a general detail about the database, and if it's a query, generate the SQL query that answers the question. If it's a general detail, provide a concise answer based on the schema summary. If you cannot answer the question based on the schema summary, respond with "I cannot answer this question based on the provided schema summary."
            """.formatted(schema, question);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> result) {
        if (result == null || result.get("candidates") == null) {
            throw new IllegalStateException("Gemini returned no candidates.");
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) result.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalStateException("Gemini returned no candidates.");
        }

        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}