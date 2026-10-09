package com.ribina.ribinamart.chatbot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MockChatProviderTest {

    private MockChatProvider provider;

    @BeforeEach
    public void setUp() {
        provider = new MockChatProvider();
    }

    @Test
    public void testGreetingResponses() {
        String reply = provider.generateReply("Hello there", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("welcome to ribinamart"));

        reply = provider.generateReply("good morning", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("assistant"));
    }

    @Test
    public void testOrderTrackingResponses() {
        String reply = provider.generateReply("Where is my order? Can I track it?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("my orders"));
        assertTrue(reply.contains("SHIPPED"));
    }

    @Test
    public void testShippingResponses() {
        String reply = provider.generateReply("How long does shipping delivery take?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.contains("3 to 5 business days"));
    }

    @Test
    public void testReturnAndRefundResponses() {
        String reply = provider.generateReply("What is your refund and return policy?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.contains("7-day return policy"));
        assertTrue(reply.contains("48 hours"));
    }

    @Test
    public void testPaymentResponses() {
        String reply = provider.generateReply("What payment methods can I use?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.contains("UPI"));
        assertTrue(reply.contains("Credit/Debit"));
    }

    @Test
    public void testWishlistResponses() {
        String reply = provider.generateReply("How do I save a product to my wishlist?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("wishlist"));
    }

    @Test
    public void testSellerResponses() {
        String reply = provider.generateReply("How do I become a seller?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.contains("Seller Dashboard"));
    }

    @Test
    public void testSecurityAndBcryptResponses() {
        String reply = provider.generateReply("Is my password and account secure?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.contains("BCrypt"));
    }

    @Test
    public void testFallbackResponses() {
        String reply = provider.generateReply("What is quantum entanglement thermodynamics?", "sess-1");
        assertNotNull(reply);
        assertTrue(reply.toLowerCase().contains("what would you like to know more about"));
    }

    @Test
    public void testProviderProperties() {
        assertEquals("MockChatProvider", provider.getProviderName());
        assertTrue(provider.isAvailable());
    }
}
