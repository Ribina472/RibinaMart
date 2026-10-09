# RibinaMart — Live Demonstration & Viva Defense Script

**Course**: Anna University R2025 Semester 3 Capstone  
**Target Environment**: `http://localhost:8085/ribinamart`  
**Duration**: ~10–12 Minutes  
**Demonstrator**: Ribina  

---

## 📋 PRE-DEMO CHECKLIST

1. Ensure the embedded application server is running:
   ```bash
   mvn compile exec:java
   ```
2. Open your web browser at: `http://localhost:8085/ribinamart`
3. Have seed test credentials ready:
   - **Admin**: `admin@ribinamart.com` / `Admin@123`
   - **Seller 1**: `seller1@ribinamart.com` / `Seller@123`
   - **Buyer 1**: `buyer1@ribinamart.com` / `Buyer@123`

---

## 🎬 STEP-BY-STEP DEMO PROCEDURE

### ⏱️ Act 1: Platform Health & Public Catalog Discovery (2 mins)

1. **Verify Backend Health**:
   - In browser or terminal, open: `http://localhost:8085/ribinamart/api/v1/health`
   - **Examiner Talking Point**: *"Our deployment health endpoint responds with HTTP 200 and confirms both Tomcat servlet container and HikariCP database connection pool are fully operational."*
2. **Catalog Browsing & Search**:
   - Navigate to the homepage: `http://localhost:8085/ribinamart/products`
   - Test category filter by selecting **"Electronics"** or **"Stationery"**.
   - Test search box with keyword **"Casio"** or **"Lab"**.
   - Note the real-time stock badges (`In Stock (X units)` or `Out of Stock`).

---

### ⏱️ Act 2: Generative AI Chatbot Assistant (2 mins)

1. **Open AI Assistant Widget**:
   - Click the blue floating robot icon in the bottom-right corner.
   - Observe the smooth expansion of the chat panel.
2. **Test Quick Suggestion Chips**:
   - Click the **"🚚 Shipping"** suggestion chip.
   - Instant response received: *"Standard shipping at RibinaMart takes 3 to 5 business days across India..."*
   - Click the **"🔄 Returns"** suggestion chip.
   - Response received: *"RibinaMart offers a hassle-free 7-day return policy..."*
3. **Test Custom Natural Language Query**:
   - Type: *"How can I sell products on RibinaMart?"* and press Enter.
   - Response received explaining the Seller registration process and Seller Dashboard.
4. **Examiner Talking Point**:
   - *"The AI chatbot utilizes a Strategy Pattern. When an external Gemini API key is configured, it invokes Google Gemini; if offline or during local network evaluation, it seamlessly resolves via the intelligent MockChatProvider with zero latency and 100% availability."*

---

### ⏱️ Act 3: Buyer Experience: Wishlist, Cart & Atomic Checkout (3 mins)

1. **Login as Buyer**:
   - Click **Login** in the top navigation.
   - Enter `buyer1@ribinamart.com` / `Buyer@123`.
   - Observe the user badge displaying **`BUYER`**.
2. **Test Wishlist (Save-for-Later)**:
   - Click on any product (e.g. *Scientific Calculator*).
   - Click the **"Wishlist"** heart button.
   - Navigate to **"My Wishlist"** in the header.
   - Show the saved item in the wishlist table.
   - Click **"Add to Cart"** directly from the Wishlist view.
3. **Cart & Atomic Checkout**:
   - Navigate to the Shopping Cart: `http://localhost:8085/ribinamart/cart`.
   - Increase or adjust quantity; verify dynamic total recalculation.
   - Click **"Proceed to Checkout"**.
   - Enter shipping address: *"Hostel 4, Anna University Campus, Chennai - 600025"*.
   - Select **"Mock Payment / Campus Pay"** and click **"Confirm & Pay"**.
4. **Order Confirmation & Verified Review**:
   - Redirects to **"My Orders"** with green success banner.
   - Show the order with status **`PENDING`** and unique Order ID.
   - Click **"Write a Review"** for the purchased product.
   - Give **5 Stars** and comment: *"Excellent textbook condition, delivered on time!"*.
   - View the product details page; show the verified review with 5 yellow stars.

---

### ⏱️ Act 4: Seller Experience & Sales Analytics Dashboard (2 mins)

1. **Login as Seller**:
   - Log out, then log in as `seller1@ribinamart.com` / `Seller@123`.
   - Click **"Seller Dashboard"** in the top navigation.
2. **Demonstrate Sales Analytics KPIs (O3)**:
   - Highlight the 5 KPI summary cards at the top:
     - **Total Revenue**: Accumulated ₹ earnings from confirmed orders.
     - **Orders Received**: Count of distinct incoming buyer orders.
     - **Units Sold**: Total volume of products sold.
     - **Active Items**: Count of currently published listings.
     - **Low Stock Alerts**: Count of items with stock ≤ 5.
3. **Incoming Orders Fulfillment**:
   - Click **"Incoming Orders"** in the navigation.
   - Find the newly placed buyer order.
   - Change status from `PENDING` to `SHIPPED`.
   - Log back in as the buyer to verify the order status updated to `SHIPPED` in real-time.

---

### ⏱️ Act 5: Administrator Governance (1 min)

1. **Login as Admin**:
   - Log in as `admin@ribinamart.com` / `Admin@123`.
   - Click **"Admin Panel"** (`/admin/dashboard`).
2. **Platform Moderation**:
   - Show the total system metrics (Users, Products, Orders, Revenue).
   - Find an active product listing and click **"Deactivate"**.
   - Show the status changes to `INACTIVE`.
   - Visit the public catalog (`/products`) and confirm the deactivated item is immediately hidden from buyers.

---

### ⏱️ Act 6: Automated Test Suite Defense (1 min)

1. **Run Maven Test Suite**:
   - In terminal, execute:
     ```bash
     mvn test
     ```
2. **Showcase Results**:
   - Show the output: **48 Tests Run, 0 Failures, 0 Errors, BUILD SUCCESS**.
   - Point out unit and integration tests covering DAOs, Services, Transactions, Security, and Chatbot.

---

## 🏆 SUMMARY WRAP-UP FOR VIVA PANEL

> *"Respected examiners, as demonstrated across all three user personas and our automated test suite, RibinaMart fulfills every functional requirement (F1–F8) and optional extension (O1 Wishlist, O3 Seller Analytics, O4 AI Chatbot) with enterprise architectural rigor, robust security, and 100% test pass rate. Thank you!"*
