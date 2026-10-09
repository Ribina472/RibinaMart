# RibinaMart — Project Defense Slide Deck (12 Slides)
### Anna University R2025 Semester 3 CSE Capstone Project Defense
**Student Name**: Ribina | **Package**: `com.ribina.ribinamart` | **Date**: October 10, 2026

---

## 📽️ SLIDE 1: Title & Project Overview

### **RibinaMart: An Enterprise-Grade Multi-Vendor Campus Marketplace with Generative AI Assistance**
- **Course**: Anna University Regulations 2025 — Semester 3 Capstone
- **Track**: Full-Stack Enterprise Java Web Architecture
- **Presenter**: Ribina (B.E. Computer Science and Engineering)
- **Tech Stack**: Java 17, Servlets 3.1, JSP, HikariCP, H2 Database, Apache Tomcat 9, Bootstrap 5, Gemini API

> **Speaker Notes**:
> "Respected examiners, good morning. Today I am proud to present my semester 3 capstone project, RibinaMart. RibinaMart is an enterprise-grade multi-vendor campus e-commerce platform built strictly using standard Java Servlets, JDBC, and Apache Tomcat, enhanced with an intelligent Generative AI customer support system."

---

## 📽️ SLIDE 2: Problem Statement & Motivation

### **Why Campus Commerce Needs a Dedicated Platform**
- **Disorganized Channels**: Buying textbooks, lab gear, and electronics via informal social groups lacks structure.
- **Security & Fraud Risks**: No identity verification for sellers or buyers; cash transactions without receipts.
- **Stock & Inventory Chaos**: No real-time stock synchronization leading to double-selling.
- **Fake or Unverified Reviews**: Reviews are unverified and prone to manipulation.

> **Speaker Notes**:
> "Campus communities struggle with informal buying and selling. Students lack a secure platform with real-time stock control, transactional guarantees, and authentic reviews from verified purchasers."

---

## 📽️ SLIDE 3: Project Objectives & Scope

### **Core Engineering Objectives**
1. **Multi-Role Governance**: Tailored experiences for Buyers, Campus Sellers, and Platform Administrators.
2. **ACID Transactional Guarantees**: Atomic stock decrement and order generation to eliminate inventory race conditions.
3. **Verified Reviews (F8)**: Feedback restricted strictly to confirmed purchasers.
4. **Intelligent AI Support (O4)**: Floating chatbot widget with hybrid strategy (Gemini API + offline domain fallback).
5. **Buyer Wishlist (O1)** & **Seller Analytics Dashboard (O3)**.

> **Speaker Notes**:
> "Our objective was to build a complete, resilient system fulfilling all requirements from Week 1 to Week 11 with zero broken workflows."

---

## 📽️ SLIDE 4: System Architecture

### **Layered Model-View-Controller (MVC) Pattern**
```
Browser / AJAX Widget
       │
       ▼
[ Filter Chain ] ── AuthFilter, EncodingFilter
       │
       ▼
[ Controller Layer ] ── Servlets (Product, Cart, Order, ChatServlet)
       │
       ▼
[ Service Layer ] ── Business Rules & Transactions (OrderService, ChatService)
       │
       ▼
[ DAO Layer ] ── SQL PreparedStatements (ProductDAO, OrderDAO, WishlistDAO)
       │
       ▼
[ HikariCP Connection Pool ] ── H2 Persistent Storage
```

> **Speaker Notes**:
> "We follow strict separation of concerns. Controllers handle HTTP lifecycle, Services handle business transactions, and DAOs isolate database interactions using HikariCP connection pooling."

---

## 📽️ SLIDE 5: Relational Database Design (D1)

### **Normalized Schema with Integrity Constraints**
- `users`: Managed roles (`BUYER`, `SELLER`, `ADMIN`), unique email, BCrypt password hashes.
- `products`: Category, price, stock quantity, status (`ACTIVE`/`INACTIVE`), foreign key to seller.
- `cart_items`: User-product cart persistence with stock boundaries.
- `orders` & `order_items`: Master-detail relationship preserving purchase price history.
- `reviews`: Restricted by foreign keys to verified orders, 1–5 rating check constraint.
- `wishlist_items`: Unique composite constraint `(user_id, product_id)` for save-for-later.

> **Speaker Notes**:
> "Our database schema is fully normalized in 3NF with foreign keys, cascading deletes where appropriate, unique constraints, and check constraints on rating values."

---

## 📽️ SLIDE 6: Core Features (F1 – F8)

### **End-to-End E-Commerce Lifecycle**
- **Authentication (F1)**: Secure login/registration with role dispatching and session management.
- **Seller Catalog (F2)**: Create, update, soft-delete listings with live inventory counts.
- **Buyer Discovery (F3)**: Real-time keyword search and category filters.
- **Cart & Checkout (F4, F5)**: Atomic multi-table checkout transaction with stock protection.
- **Order Tracking (F6)**: Order status progression (`PENDING` → `CONFIRMED` → `SHIPPED` → `DELIVERED`).
- **Admin Panel (F7)**: Deactivate rogue listings and inspect user rosters.
- **Verified Reviews (F8)**: Review submissions verified against past delivered orders.

> **Speaker Notes**:
> "Every functional requirement from F1 through F8 is fully operational with complete UI feedback and data validation."

---

## 📽️ SLIDE 7: Generative AI Chatbot Assistant (O4)

### **Resilient Strategy Pattern & Fallback Architecture**
- **Primary Provider**: `GeminiChatProvider` calling Google Gemini REST API with 4-second timeout.
- **Fallback Provider**: `MockChatProvider` containing 12+ domain knowledge categories (shipping, returns, tracking, selling, security).
- **Rate Limiting**: Per-session sliding window limiting queries to 10 requests/minute.
- **In-Memory Cache**: Concurrent cache serving repeated queries with sub-millisecond response.
- **Frontend Widget**: Sleek floating UI with suggestion chips and persistent session history.

> **Speaker Notes**:
> "The chatbot employs the Strategy Pattern. If external internet is down or API quotas expire, it falls back to the intelligent MockChatProvider with zero downtime."

---

## 📽️ SLIDE 8: Advanced Extensions: Wishlist & Analytics

### **O1: Save-for-Later Wishlist**
- Allows buyers to bookmark products with one click.
- Dedicated view at `/wishlist` with "Move to Cart" and "Remove" actions.
- Preserves saved items across login sessions.

### **O3: Seller Sales Analytics**
- Real-time KPI summary on Seller Dashboard:
  - Total Sales Revenue (₹)
  - Total Orders Received
  - Total Units Sold
  - Active Listings Count
  - Low Stock Warning Alerts (Inventory ≤ 5 units)

> **Speaker Notes**:
> "We implemented both optional features O1 and O3. Sellers have actionable business intelligence with live KPI metrics and low-stock alerts."

---

## 📽️ SLIDE 9: Security Engineering & Threat Defense

### **Enterprise-Grade Vulnerability Mitigation**
- **SQL Injection**: **100% PreparedStatement usage** with bind parameters; zero raw string concatenation.
- **XSS Prevention**: JSTL `<c:out value="..."/>` sanitizes all dynamic user text across every JSP.
- **Credential Protection**: **BCrypt** hashing with salt factor 12.
- **Access Control**: `AuthFilter` intercepts protected URLs (`/admin/*`, `/seller/*`, `/cart/*`, `/wishlist/*`).
- **Session Security**: 30-minute session expiration, credential invalidation on logout.

> **Speaker Notes**:
> "Security is baked into the foundation. We conducted security audits confirming complete mitigation against OWASP Top 10 vulnerabilities including SQLi and XSS."

---

## 📽️ SLIDE 10: Testing & Quality Assurance

### **Automated Test Results (JUnit 5 + Mockito)**
- **Total Tests**: **48 Tests Executed**
- **Failures / Errors**: **0**
- **Pass Rate**: **100%**
- **Test Categories**:
  - DAO Layer: `CartDAOTest`, `OrderDAOTest`, `ProductDAOTest`, `UserDAOTest`, `WishlistDAOTest`
  - Service Layer: `UserServiceTest`, `OrderServiceTest`, `ProductServiceTest`, `CartServiceTest`, `WishlistServiceTest`, `SellerAnalyticsTest`
  - AI Chatbot: `ChatServiceTest`, `MockChatProviderTest`

> **Speaker Notes**:
> "Our automated test suite runs via Maven Surefire, testing isolated database transactions with in-memory H2 and mocking external dependencies with Mockito."

---

## 📽️ SLIDE 11: Live Demonstration Highlights

### **Key Workflows Demonstrated**
1. **Catalog & Search**: Filtering products by category and searching by keyword.
2. **Buyer Flow**: Adding to Wishlist, moving to Cart, atomic checkout, viewing orders.
3. **AI Chatbot**: Asking about return policy, order delivery times, and shipping rates.
4. **Seller Flow**: Viewing sales analytics KPIs, updating inventory stock, changing order status.
5. **Admin Flow**: Toggling product visibility and auditing platform users.

> **Speaker Notes**:
> "In our live demonstration, we will showcase all three user roles and test both the online and offline capabilities of the AI Assistant."

---

## 📽️ SLIDE 12: Conclusion & Future Scope

### **Summary of Achievements**
- Successfully engineered an end-to-end multi-vendor marketplace adhering strictly to Anna University R2025 guidelines.
- Met all September 21 and October 10 milestones ahead of schedule.
- Packaged as a portable, standalone deployable application.

### **Future Roadmap**
- Real-time payment gateway webhooks (Razorpay / Stripe).
- WebSocket-based real-time seller order notifications.
- Collaborative filtering AI recommendation engine.

**Thank You! Questions & Discussion**

> **Speaker Notes**:
> "Thank you for your time and guidance. I invite any questions from the panel."
