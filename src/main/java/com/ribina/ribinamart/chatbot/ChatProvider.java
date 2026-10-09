package com.ribina.ribinamart.chatbot;

/**
 * Interface defining the AI Chatbot strategy for generating customer assistance replies.
 * Follows the Strategy Pattern to allow seamless switching between Mock and Gemini providers.
 */
public interface ChatProvider {

    /**
     * Generates an intelligent reply to the user message.
     *
     * @param userMessage the message entered by the customer
     * @param sessionId the session identifier for conversation continuity
     * @return the assistant response text
     */
    String generateReply(String userMessage, String sessionId);

    /**
     * Returns the name of the provider (e.g. "MockChatProvider" or "GeminiChatProvider").
     */
    String getProviderName();

    /**
     * Indicates whether this provider is currently configured and operational.
     */
    boolean isAvailable();
}
