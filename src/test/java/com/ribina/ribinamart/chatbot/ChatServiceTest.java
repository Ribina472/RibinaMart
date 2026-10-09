package com.ribina.ribinamart.chatbot;

import com.ribina.ribinamart.dto.ChatResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatProvider mockPrimary;

    @Mock
    private ChatProvider mockFallback;

    private ChatService chatService;

    @BeforeEach
    public void setUp() {
        chatService = new ChatService(mockPrimary, mockFallback);
    }

    @Test
    public void testPrimaryProviderSuccess() {
        when(mockPrimary.isAvailable()).thenReturn(true);
        when(mockPrimary.getProviderName()).thenReturn("GeminiChatProvider");
        when(mockPrimary.generateReply(anyString(), anyString())).thenReturn("This is Gemini's response.");

        ChatResponseDTO response = chatService.processMessage("Hello", "sess-test-1");
        assertNotNull(response);
        assertEquals("This is Gemini's response.", response.getReply());
        assertEquals("GeminiChatProvider", response.getProvider());
    }

    @Test
    public void testFallbackWhenPrimaryFails() {
        when(mockPrimary.isAvailable()).thenReturn(true);
        when(mockPrimary.generateReply(anyString(), anyString())).thenThrow(new RuntimeException("API Timeout"));

        when(mockFallback.getProviderName()).thenReturn("MockChatProvider");
        when(mockFallback.generateReply(anyString(), anyString())).thenReturn("Mock fallback response.");

        ChatResponseDTO response = chatService.processMessage("Can I return an item?", "sess-test-2");
        assertNotNull(response);
        assertEquals("Mock fallback response.", response.getReply());
        assertEquals("MockChatProvider", response.getProvider());
    }

    @Test
    public void testRateLimitingEnforcement() {
        when(mockFallback.getProviderName()).thenReturn("MockChatProvider");
        when(mockFallback.generateReply(anyString(), anyString())).thenReturn("OK");

        String session = "rate-limit-session";
        // Send 10 allowed messages
        for (int i = 0; i < 10; i++) {
            ChatResponseDTO res = chatService.processMessage("Message " + i, session);
            assertEquals("OK", res.getReply());
        }

        // 11th message should trigger rate limiter
        ChatResponseDTO throttled = chatService.processMessage("Message 11", session);
        assertEquals("RateLimiter", throttled.getProvider());
        assertTrue(throttled.getReply().contains("too quickly"));
    }

    @Test
    public void testOverlyLongMessageRejected() {
        String longText = "a".repeat(501);
        ChatResponseDTO response = chatService.processMessage(longText, "sess-test-3");

        assertEquals("Validator", response.getProvider());
        assertTrue(response.getReply().contains("too long"));
    }

    @Test
    public void testQueryCaching() {
        when(mockFallback.getProviderName()).thenReturn("MockChatProvider");
        when(mockFallback.generateReply("delivery time", "sess-test-4")).thenReturn("Takes 3-5 days");

        // First call populates cache
        ChatResponseDTO first = chatService.processMessage("delivery time", "sess-test-4");
        assertEquals("Takes 3-5 days", first.getReply());
        assertEquals("MockChatProvider", first.getProvider());

        // Second call should be served from cache
        ChatResponseDTO second = chatService.processMessage("delivery time", "sess-test-4");
        assertEquals("Takes 3-5 days", second.getReply());
        assertTrue(second.getProvider().startsWith("Cache"));

        // Verify fallback generateReply was only invoked once
        verify(mockFallback, times(1)).generateReply("delivery time", "sess-test-4");
    }

    @Test
    public void testSanitizationOfHtmlInput() {
        when(mockFallback.getProviderName()).thenReturn("MockChatProvider");
        when(mockFallback.generateReply("&lt;script&gt;alert(1)&lt;/script&gt;", "sess-test-5"))
                .thenReturn("Safe response");

        ChatResponseDTO res = chatService.processMessage("<script>alert(1)</script>", "sess-test-5");
        assertEquals("Safe response", res.getReply());
    }
}
