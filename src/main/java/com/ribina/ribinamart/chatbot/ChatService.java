package com.ribina.ribinamart.chatbot;

import com.ribina.ribinamart.dto.ChatResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service orchestrating chatbot interactions, rate limiting, in-memory caching,
 * input validation, and provider fallback mechanism.
 */
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private static final int MAX_INPUT_LENGTH = 500;
    private static final int RATE_LIMIT_MAX_REQUESTS = 10;
    private static final long RATE_LIMIT_WINDOW_MS = 60_000L; // 1 minute

    private final ChatProvider primaryProvider;
    private final ChatProvider fallbackProvider;

    // Rate limiter: sessionId -> queue of request epoch timestamps
    private final Map<String, Deque<Long>> rateLimiters = new ConcurrentHashMap<>();

    // In-memory query cache: normalized message -> cached reply
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public ChatService() {
        this(new GeminiChatProvider(), new MockChatProvider());
    }

    public ChatService(ChatProvider primaryProvider, ChatProvider fallbackProvider) {
        this.primaryProvider = primaryProvider;
        this.fallbackProvider = fallbackProvider;
    }

    /**
     * Processes customer input, applies security and rate limiting filters,
     * checks cache, and resolves the response via the active provider strategy.
     */
    public ChatResponseDTO processMessage(String rawMessage, String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = "anonymous-guest";
        }

        // 1. Rate Limiting Check
        if (isRateLimited(sessionId)) {
            LOGGER.warn("Rate limit exceeded for chatbot session: {}", sessionId);
            return new ChatResponseDTO(
                    "You are sending messages too quickly. Please wait a moment before sending another message.",
                    "RateLimiter"
            );
        }

        // 2. Input Validation & Trimming
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return new ChatResponseDTO(
                    "Hello! How can I assist you today? Feel free to ask about orders, shipping, or products.",
                    "System"
            );
        }

        String sanitized = sanitizeInput(rawMessage);
        if (sanitized.length() > MAX_INPUT_LENGTH) {
            return new ChatResponseDTO(
                    "Your message is too long. Please keep your question under " + MAX_INPUT_LENGTH + " characters.",
                    "Validator"
            );
        }

        String normalizedKey = sanitized.toLowerCase().trim();

        // 3. Cache Lookup
        if (cache.containsKey(normalizedKey)) {
            LOGGER.debug("Serving chatbot reply from in-memory cache for query: {}", normalizedKey);
            return new ChatResponseDTO(cache.get(normalizedKey), "Cache (" + fallbackProvider.getProviderName() + ")");
        }

        // 4. Primary Provider Attempt (e.g. Gemini)
        String reply = null;
        String providerUsed = null;

        if (primaryProvider != null && primaryProvider.isAvailable()) {
            try {
                reply = primaryProvider.generateReply(sanitized, sessionId);
                if (reply != null && !reply.trim().isEmpty()) {
                    providerUsed = primaryProvider.getProviderName();
                }
            } catch (Exception e) {
                LOGGER.warn("Primary chat provider failed: {}. Falling back to default provider.", e.getMessage());
            }
        }

        // 5. Fallback Provider (MockChatProvider)
        if (reply == null || reply.trim().isEmpty()) {
            reply = fallbackProvider.generateReply(sanitized, sessionId);
            providerUsed = fallbackProvider.getProviderName();
        }

        // 6. Cache the successful reply (capping cache size to prevent memory leaks)
        if (cache.size() < 1000) {
            cache.put(normalizedKey, reply);
        }

        return new ChatResponseDTO(reply, providerUsed);
    }

    private synchronized boolean isRateLimited(String sessionId) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = rateLimiters.computeIfAbsent(sessionId, k -> new ArrayDeque<>());

        // Evict expired timestamps older than 60 seconds
        while (!timestamps.isEmpty() && (now - timestamps.peekFirst()) > RATE_LIMIT_WINDOW_MS) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= RATE_LIMIT_MAX_REQUESTS) {
            return true;
        }

        timestamps.addLast(now);
        return false;
    }

    private String sanitizeInput(String input) {
        return input.trim()
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public void clearCache() {
        cache.clear();
    }
}
