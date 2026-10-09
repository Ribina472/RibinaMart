package com.ribina.ribinamart.chatbot;

import java.util.Locale;

/**
 * High-fidelity offline rule-based Mock Chatbot Provider.
 * Provides instant, zero-latency, realistic e-commerce assistance across 12+ categories
 * without requiring an external internet connection or API credentials.
 */
public class MockChatProvider implements ChatProvider {

    @Override
    public String generateReply(String userMessage, String sessionId) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! How can I assist you with your shopping experience at RibinaMart today?";
        }

        String msg = userMessage.toLowerCase(Locale.ROOT).trim();

        // 1. Greetings
        if (matchesAny(msg, "hi", "hello", "hey", "good morning", "good evening", "good afternoon", "greetings")) {
            return "Hello! Welcome to RibinaMart. I'm your virtual shopping assistant. How can I help you today? You can ask about tracking orders, shipping times, returns, payment methods, or becoming a seller!";
        }

        // 2. Order Tracking & Status
        if (matchesAny(msg, "track", "order status", "where is my order", "order delivery", "my orders")) {
            return "You can view and track all your orders by logging in and navigating to the 'My Orders' section in the navigation menu. Each order displays its current status: PENDING, CONFIRMED, SHIPPED, DELIVERED, or CANCELLED.";
        }

        // 3. Shipping & Delivery
        if (matchesAny(msg, "ship", "delivery", "shipping time", "how long to deliver", "dispatch", "courier")) {
            return "Standard shipping at RibinaMart takes 3 to 5 business days across India. Express shipping arrives in 1 to 2 business days. Orders above ₹499 qualify for free standard shipping!";
        }

        // 4. Returns & Refunds
        if (matchesAny(msg, "return", "refund", "exchange", "money back", "cancel order", "cancellation")) {
            return "RibinaMart offers a hassle-free 7-day return policy for unused, damaged, or defective items. Once the return is inspected, your refund will be processed back to your original payment method within 48 hours.";
        }

        // 5. Payment Methods
        if (matchesAny(msg, "payment", "pay", "credit card", "upi", "debit card", "cash on delivery", "cod", "net banking")) {
            return "We accept all major Credit/Debit Cards (Visa, MasterCard, RuPay), UPI (Google Pay, PhonePe, Paytm), Net Banking, and simulated Mock Payment for our academic demonstration environment.";
        }

        // 6. Wishlist / Save for Later
        if (matchesAny(msg, "wishlist", "save for later", "favorite", "save product", "bookmark")) {
            return "Found something you like? Click the 'Add to Wishlist' button on any product details page to save it for later. You can access all your saved items anytime from your Wishlist tab in the header menu.";
        }

        // 7. Seller Registration & Selling
        if (matchesAny(msg, "sell", "seller", "vendor", "list product", "become a seller", "merchant")) {
            return "To sell on RibinaMart, simply register an account choosing the 'Seller' role. Once registered, you will gain access to the Seller Dashboard where you can list products, manage inventory, view incoming buyer orders, and track sales analytics!";
        }

        // 8. Reviews & Ratings
        if (matchesAny(msg, "review", "rating", "feedback", "star", "comment", "verified buyer")) {
            return "At RibinaMart, reviews are transparent and trustworthy! Only verified buyers who have successfully purchased an item can submit a review and rating (1 to 5 stars) from their completed order details page.";
        }

        // 9. Account & Security
        if (matchesAny(msg, "password", "security", "login", "register", "signup", "auth", "bcrypt", "account")) {
            return "Your security is our top priority. RibinaMart encrypts all user credentials using industry-standard BCrypt hashing with strong salting. Your passwords are never stored in plain text.";
        }

        // 10. Discounts & Offers
        if (matchesAny(msg, "discount", "offer", "coupon", "promo", "deal", "sale", "cheap")) {
            return "Check out our latest seasonal promotions on the RibinaMart homepage! Students can also enjoy special introductory campus offers across select categories.";
        }

        // 11. Customer Support / Contact
        if (matchesAny(msg, "contact", "support", "help", "email", "phone", "agent", "human", "customer service")) {
            return "Need personal assistance? You can reach our 24/7 customer care team via email at support@ribinamart.com or call our toll-free student helpline at 1800-RIBINA-MART.";
        }

        // 12. About RibinaMart
        if (matchesAny(msg, "about", "who are you", "what is ribinamart", "ribina", "anna university", "capstone")) {
            return "RibinaMart is a full-featured, secure e-commerce marketplace platform engineered as an Anna University R2025 Semester 3 Computer Science capstone project. It connects buyers, independent sellers, and marketplace administrators with enterprise-grade Java web technologies.";
        }

        // 13. Gratitude / Closing
        if (matchesAny(msg, "thank", "thanks", "bye", "goodbye", "awesome", "great")) {
            return "You're very welcome! If you have any more questions, feel free to ask. Happy shopping with RibinaMart!";
        }

        // Default Fallback
        return "I'm not completely certain about that specific query, but I can certainly assist you with: "
                + "1) Order tracking and status, "
                + "2) Shipping & delivery timelines, "
                + "3) 7-day return and refund policies, "
                + "4) Payment methods, "
                + "5) Wishlist and saved items, or "
                + "6) How to become a seller. "
                + "What would you like to know more about?";
    }

    private boolean matchesAny(String input, String... keywords) {
        for (String keyword : keywords) {
            if (keyword.length() <= 3) {
                // Word boundary check for short words like "hi", "hey", "pay", "cod", "upi"
                if (input.matches(".*\\b" + java.util.regex.Pattern.quote(keyword) + "\\b.*")) {
                    return true;
                }
            } else {
                if (input.contains(keyword)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getProviderName() {
        return "MockChatProvider";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
