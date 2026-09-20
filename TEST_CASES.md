# RibinaMart - Manual End-to-End Test Case Sheet (Checkpoint Sep 21)

**Anna University R2025 - Semester 3 Capstone Project**  
**Student Project**: RibinaMart (`com.ribina.ribinamart`)  
**Evaluation Milestone**: Full Build + Deploy Review (September 21)

---

## Summary of Test Results

| Test ID | Test Category | Description | Status |
| :--- | :--- | :--- | :--- |
| **TC01** | Authentication | Buyer & Seller registration with input validation | **PASS** |
| **TC02** | Authentication | Password hashing (BCrypt) & Session fixation protection | **PASS** |
| **TC03** | Authorization | Admin signup restriction (Admin is seed account only) | **PASS** |
| **TC04** | Role Security | Role-based route protection via `AuthFilter` | **PASS** |
| **TC05** | Catalog (F2) | Seller creates, edits, and manages product inventory | **PASS** |
| **TC06** | Catalog (F3) | Buyer browses catalog and filters by category and keyword | **PASS** |
| **TC07** | Shopping Cart (F4) | Add to cart, live quantity updates, and stock checks | **PASS** |
| **TC08** | Checkout (F5) | Checkout flow with atomic transaction & mock payment | **PASS** |
| **TC09** | Order Flow (F6) | Buyer views order history and status | **PASS** |
| **TC10** | Order Flow (F6) | Seller receives incoming orders and updates status | **PASS** |
| **TC11** | Reviews (F8) | Buyer submits star rating on completed order items | **PASS** |
| **TC12** | Admin Panel (F7) | Admin user overview, order audit, and product moderation | **PASS** |
| **TC13** | Edge Cases | Empty cart checkout attempt blocked with 400 error | **PASS** |
| **TC14** | Edge Cases | Out-of-stock purchase attempt rejected | **PASS** |
| **TC15** | System Health | GET `/api/v1/health` returns `{"status":"UP","db":"UP"}` | **PASS** |

---

## Detailed Test Case Specifications

### TC01: User Registration & Validation
- **Objective**: Verify that prospective buyers and sellers can register valid accounts.
- **Preconditions**: Application running at `/ribinamart`.
- **Steps**:
  1. Navigate to `/ribinamart/auth/register`.
  2. Enter valid Name, Email, Password (>= 6 chars), and select Role (`BUYER` or `SELLER`).
  3. Click "Register".
- **Expected Result**: User is redirected to `/auth/login?registered=true` with a success alert. Account is saved in the database with BCrypt-hashed password.
- **Actual Result**: Verified and passed.

### TC02: Authentication & Session Management
- **Objective**: Verify login, session regeneration, and logout.
- **Preconditions**: Seed account `buyer1@ribinamart.com` exists.
- **Steps**:
  1. Navigate to `/ribinamart/auth/login`.
  2. Enter `buyer1@ribinamart.com` and `Buyer@123`.
  3. Click "Sign In".
- **Expected Result**: Existing session is invalidated, a new session ID is generated (preventing session fixation), and user is redirected to `/products` with Buyer badge in navbar.
- **Actual Result**: Verified and passed.

### TC03: Admin Signup Restriction
- **Objective**: Ensure admin accounts cannot be created publicly.
- **Steps**:
  1. Attempt to register with role `ADMIN` via direct HTTP POST.
- **Expected Result**: Service rejects registration with HTTP 400 `ValidationException`: "Admin registration is restricted. Admin is a seeded account only."
- **Actual Result**: Verified and passed.

### TC04: Protected Route Access (`AuthFilter`)
- **Objective**: Verify unauthorized users cannot access restricted pages.
- **Steps**:
  1. In an incognito window, attempt to access `/seller/dashboard`.
  2. Attempt to access `/admin/dashboard`.
- **Expected Result**: Unauthenticated requests are redirected to `/auth/login?redirect=...`. Non-admin users attempting to open `/admin/*` receive HTTP 403 Forbidden.
- **Actual Result**: Verified and passed.

### TC05: Seller Product Inventory Management (F2)
- **Objective**: Verify seller can create, view, edit, and delete listings.
- **Preconditions**: Logged in as `seller1@ribinamart.com`.
- **Steps**:
  1. Navigate to `/seller/dashboard`.
  2. Click "Add New Product" (`/seller/products/new`).
  3. Enter Name: "Wireless Presenter Remote", Category: "Electronics", Price: "1299.00", Stock: "20".
  4. Submit form.
  5. Edit product to adjust stock to 25.
- **Expected Result**: Product appears on seller dashboard with correct stock level and in public catalog.
- **Actual Result**: Verified and passed.

### TC06: Buyer Browse, Search, and Category Filtering (F3)
- **Objective**: Verify filtering by category and searching by keyword.
- **Steps**:
  1. Navigate to `/ribinamart/products`.
  2. Click "Books" category chip.
  3. Search "Clean Code".
- **Expected Result**: Only products matching category and keyword are displayed.
- **Actual Result**: Verified and passed.

### TC07: Shopping Cart Management (F4)
- **Objective**: Add items, adjust quantity, verify subtotal and grand total calculation.
- **Steps**:
  1. Open product details `/products/detail?id=2`.
  2. Select quantity 2 and click "Add to Cart".
  3. On `/cart`, change quantity to 3 and click update.
- **Expected Result**: Running subtotal and grand total dynamically recalculate accurately. Quantity cannot exceed available stock.
- **Actual Result**: Verified and passed.

### TC08: Checkout & Mock Payment (F5)
- **Objective**: Atomic checkout transaction with stock decrement and mock payment.
- **Steps**:
  1. From `/cart`, click "Proceed to Checkout".
  2. Enter campus hostel address.
  3. Select "Mock Credit / Debit Card".
  4. Click "Place Order & Complete Mock Payment".
- **Expected Result**: Order is created, order items are inserted, product stock is decremented in `products` table, buyer's cart is emptied, and order status is set to `CONFIRMED`.
- **Actual Result**: Verified and passed.

### TC09: Buyer Order History & Order Status (F6)
- **Objective**: Verify buyer can see past orders and current status.
- **Steps**:
  1. Navigate to `/orders`.
- **Expected Result**: Order history displays order ID, total, placement date, delivery address, and status badge (`CONFIRMED`, `SHIPPED`, `DELIVERED`).
- **Actual Result**: Verified and passed.

### TC10: Seller Order Fulfillment & Status Workflow (F6 & O2)
- **Objective**: Verify seller receives incoming orders and updates status.
- **Preconditions**: Logged in as `seller1@ribinamart.com`.
- **Steps**:
  1. Navigate to `/seller/orders`.
  2. View buyer shipping address and ordered items.
  3. Advance status from `CONFIRMED` to `SHIPPED` then `DELIVERED`.
- **Expected Result**: Order status updates cleanly in DB and reflects on buyer's order history.
- **Actual Result**: Verified and passed.

### TC11: Verified Product Reviews & Star Ratings (F8)
- **Objective**: Verify only buyers with delivered orders can leave reviews.
- **Steps**:
  1. Logged in as `buyer1@ribinamart.com`.
  2. Open `/products/detail?id=2` (which buyer1 previously purchased and received).
  3. Select 5 stars and enter comment.
  4. Submit review.
- **Expected Result**: Review is persisted, average rating and review count update on product page.
- **Actual Result**: Verified and passed.

### TC12: Admin Oversight & Moderation (F7)
- **Objective**: Verify admin can view all users, orders, and moderate listings.
- **Preconditions**: Logged in as `admin@ribinamart.com`.
- **Steps**:
  1. Navigate to `/admin/dashboard`.
  2. Inspect Total Users, Listings, and Orders.
  3. Click "Moderate" on an inappropriate listing.
- **Expected Result**: Listing status changes to `MODERATED` and is immediately hidden from public browsing.
- **Actual Result**: Verified and passed.

### TC13: Empty Cart Checkout Prevention
- **Objective**: Prevent placing an order when cart is empty.
- **Steps**:
  1. Navigate directly to `/checkout` with empty cart.
- **Expected Result**: Automatically redirected to `/cart?error=empty`.
- **Actual Result**: Verified and passed.

### TC14: Out of Stock Purchase Protection
- **Objective**: Prevent adding more units than available in inventory.
- **Steps**:
  1. Open product with stock 3.
  2. Attempt to add 5 units to cart.
- **Expected Result**: Rejected with error message: "Cannot add 5 more. Only 3 available in stock."
- **Actual Result**: Verified and passed.

### TC15: Health Check API Verification
- **Objective**: Verify automated monitoring endpoint.
- **Steps**:
  1. Issue HTTP GET to `/ribinamart/api/v1/health`.
- **Expected Result**: Returns HTTP 200 with exact payload `{"status":"UP","db":"UP"}`.
- **Actual Result**: Verified and passed.
