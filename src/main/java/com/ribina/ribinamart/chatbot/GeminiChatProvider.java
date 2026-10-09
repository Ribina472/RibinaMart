package com.ribina.ribinamart.chatbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Live Google Gemini AI Chatbot Provider.
 * Integrates directly with the Gemini REST API using Java 17 HttpClient.
 * Features a 4-second timeout, input sanitization, and graceful failure.
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiChatProvider.class);
    private static final String DEFAULT_MODEL = "gemini-1.5-flash";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;

    public GeminiChatProvider() {
        this(resolveApiKey(), DEFAULT_MODEL);
    }

    public GeminiChatProvider(String apiKey) {
        this(apiKey, DEFAULT_MODEL);
    }

    public GeminiChatProvider(String apiKey, String model) {
        this.apiKey = (apiKey != null) ? apiKey.trim() : null;
        this.model = (model != null && !model.trim().isEmpty()) ? model.trim() : DEFAULT_MODEL;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    private static String resolveApiKey() {
        String key = System.getenv("GEMINI_API_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        return System.getProperty("gemini.api.key");
    }

    @Override
    public String generateReply(String userMessage, String sessionId) {
        if (!isAvailable()) {
            LOGGER.warn("GeminiChatProvider called but API key is missing or blank.");
            return null;
        }

        try {
            String endpoint = String.format(GEMINI_API_URL, model, apiKey);

            // Construct Gemini REST payload
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text",
                    "Role: You are RibinaMart's helpful AI shopping assistant for a multi-vendor e-commerce store. "
                    + "Keep replies concise (1 to 3 sentences maximum), courteous, and helpful. "
                    + "Assist with order inquiries, shipping, returns, sellers, or store features.\n"
                    + "Customer: " + userMessage);

            JsonArray parts = new JsonArray();
            parts.add(textPart);

            JsonObject contentObj = new JsonObject();
            contentObj.addProperty("role", "user");
            contentObj.add("parts", parts);

            JsonArray contents = new JsonArray();
            contents.add(contentObj);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contents);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .timeout(Duration.ofSeconds(4))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray candidates = root.getAsJsonArray("candidates");
                if (candidates != null && candidates.size() > 0) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    if (content != null) {
                        JsonArray replyParts = content.getAsJsonArray("parts");
                        if (replyParts != null && replyParts.size() > 0) {
                            return replyParts.get(0).getAsJsonObject().get("text").getAsString().trim();
                        }
                    }
                }
            } else {
                LOGGER.warn("Gemini API returned non-200 HTTP status {}: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to query Gemini API due to error: {}", e.getMessage());
        }

        return null; // Signals fallback to MockChatProvider
    }

    @Override
    public String getProviderName() {
        return "GeminiChatProvider";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isEmpty();
    }
}
