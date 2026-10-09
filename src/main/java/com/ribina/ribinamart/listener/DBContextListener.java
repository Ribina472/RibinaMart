package com.ribina.ribinamart.listener;

import com.ribina.ribinamart.dao.*;
import com.ribina.ribinamart.model.OrderStatus;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import com.ribina.ribinamart.service.*;
import com.ribina.ribinamart.util.DBConnectionPool;
import com.ribina.ribinamart.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manages application lifecycle: HikariCP connection pool, database schema creation,
 * initial seed data population, and dependency injection via ServletContext attributes.
 */
@WebListener
public class DBContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBContextListener.class);

    public static final String ATTR_USER_SERVICE = "userService";
    public static final String ATTR_PRODUCT_SERVICE = "productService";
    public static final String ATTR_CART_SERVICE = "cartService";
    public static final String ATTR_ORDER_SERVICE = "orderService";
    public static final String ATTR_REVIEW_SERVICE = "reviewService";
    public static final String ATTR_WISHLIST_SERVICE = "wishlistService";
    public static final String ATTR_CHAT_SERVICE = "chatService";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        LOGGER.info("Initializing RibinaMart application context...");

        // 1. Load config
        Properties props = loadConfig();
        String jdbcUrl = System.getenv("DB_URL");
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = props.getProperty("db.url", "jdbc:h2:./data/ribinamart;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE");
        }
        String dbUser = System.getenv("DB_USER");
        if (dbUser == null) {
            dbUser = props.getProperty("db.user", "sa");
        }
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null) {
            dbPassword = props.getProperty("db.password", "");
        }

        // 2. Initialize Connection Pool
        DBConnectionPool.init(jdbcUrl, dbUser, dbPassword);

        // 3. Initialize Schema & Seed Data
        initDatabaseSchema();
        populateSeedDataIfEmpty();

        // 4. Instantiate DAOs & Services
        UserDAO userDAO = new UserDAOImpl();
        ProductDAO productDAO = new ProductDAOImpl();
        CartDAO cartDAO = new CartDAOImpl();
        OrderDAO orderDAO = new OrderDAOImpl();
        ReviewDAO reviewDAO = new ReviewDAOImpl();
        WishlistDAO wishlistDAO = new WishlistDAOImpl();

        UserService userService = new UserService(userDAO);
        ReviewService reviewService = new ReviewService(reviewDAO, productDAO);
        ProductService productService = new ProductService(productDAO, reviewDAO);
        CartService cartService = new CartService(cartDAO, productDAO);
        OrderService orderService = new OrderService(orderDAO, cartDAO, productDAO);
        WishlistService wishlistService = new WishlistService(wishlistDAO, productDAO);
        com.ribina.ribinamart.chatbot.ChatService chatService = new com.ribina.ribinamart.chatbot.ChatService();

        // 5. Register Services in ServletContext
        context.setAttribute(ATTR_USER_SERVICE, userService);
        context.setAttribute(ATTR_PRODUCT_SERVICE, productService);
        context.setAttribute(ATTR_CART_SERVICE, cartService);
        context.setAttribute(ATTR_ORDER_SERVICE, orderService);
        context.setAttribute(ATTR_REVIEW_SERVICE, reviewService);
        context.setAttribute(ATTR_WISHLIST_SERVICE, wishlistService);
        context.setAttribute(ATTR_CHAT_SERVICE, chatService);

        LOGGER.info("RibinaMart context initialization completed successfully.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down RibinaMart application context...");
        DBConnectionPool.shutdown();
        LOGGER.info("RibinaMart application context destroyed.");
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            LOGGER.warn("Could not load config.properties, using defaults", e);
        }
        return props;
    }

    private void initDatabaseSchema() {
        LOGGER.info("Executing schema.sql DDL...");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (is == null) {
                LOGGER.error("schema.sql not found in classpath!");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Connection conn = DBConnectionPool.getConnection();
                 Statement stmt = conn.createStatement()) {

                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("--") || line.isEmpty()) {
                        continue;
                    }
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String statementSql = sql.toString().replace(";", "").trim();
                        if (!statementSql.isEmpty()) {
                            stmt.execute(statementSql);
                        }
                        sql.setLength(0);
                    }
                }
                LOGGER.info("Database schema initialized successfully.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize database schema", e);
            throw new RuntimeException("Database schema initialization failed", e);
        }
    }

    private void populateSeedDataIfEmpty() {
        LOGGER.info("Checking if seed data needs to be populated...");
        try (Connection conn = DBConnectionPool.getConnection()) {
            boolean hasUsers;
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM users");
                 ResultSet rs = ps.executeQuery()) {
                hasUsers = rs.next() && rs.getInt(1) > 0;
            }

            if (hasUsers) {
                LOGGER.info("Database already contains user records. Skipping seed data.");
                return;
            }

            LOGGER.info("Empty database detected. Populating realistic demo seed data...");

            // 1. Seed Accounts
            // Admin (Seed account only - no public signup flow)
            long adminId = insertUser(conn, "Ribina Admin", "admin@ribinamart.com",
                    PasswordUtil.hashPassword("Admin@123"), "ADMIN");

            // Sellers
            long seller1Id = insertUser(conn, "TechHub Electronics", "seller1@ribinamart.com",
                    PasswordUtil.hashPassword("Seller@123"), "SELLER");
            long seller2Id = insertUser(conn, "PageTurner Bookstore", "seller2@ribinamart.com",
                    PasswordUtil.hashPassword("Seller@123"), "SELLER");

            // Buyers
            long buyer1Id = insertUser(conn, "Aditya Buyer", "buyer1@ribinamart.com",
                    PasswordUtil.hashPassword("Buyer@123"), "BUYER");
            long buyer2Id = insertUser(conn, "Kavya Buyer", "buyer2@ribinamart.com",
                    PasswordUtil.hashPassword("Buyer@123"), "BUYER");

            // 2. Seed Products
            long p1 = insertProduct(conn, seller1Id, "UltraBook Pro 15",
                    "15.6-inch FHD laptop with Intel Core i7, 16GB RAM, 512GB NVMe SSD, backlit keyboard, and all-day battery life.",
                    new BigDecimal("84999.00"), 12, "Electronics",
                    "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&auto=format&fit=crop&q=60");

            long p2 = insertProduct(conn, seller1Id, "Wireless Noise-Cancelling Headphones",
                    "Over-ear bluetooth headphones with 40-hour battery, active noise cancellation, and premium sound.",
                    new BigDecimal("4999.00"), 25, "Electronics",
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&auto=format&fit=crop&q=60");

            long p3 = insertProduct(conn, seller1Id, "Ergonomic Mechanical Keyboard",
                    "RGB backlit mechanical keyboard with hot-swappable brown switches and USB-C connectivity.",
                    new BigDecimal("2899.00"), 30, "Electronics",
                    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&auto=format&fit=crop&q=60");

            long p4 = insertProduct(conn, seller2Id, "Designing Data-Intensive Applications",
                    "The definitive guide to the architecture of data systems by Martin Kleppmann. Highly recommended for software engineers.",
                    new BigDecimal("1299.00"), 40, "Books",
                    "https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=500&auto=format&fit=crop&q=60");

            long p5 = insertProduct(conn, seller2Id, "Clean Code by Robert C. Martin",
                    "A Handbook of Agile Software Craftsmanship covering best practices, design patterns, and code hygiene.",
                    new BigDecimal("899.00"), 35, "Books",
                    "https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=500&auto=format&fit=crop&q=60");

            long p6 = insertProduct(conn, seller1Id, "4K UHD Smart Monitor 27-inch",
                    "IPS display with HDR400, 99% sRGB color gamut, USB-C 65W charging, and ultra-thin bezels.",
                    new BigDecimal("22499.00"), 8, "Electronics",
                    "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&auto=format&fit=crop&q=60");

            long p7 = insertProduct(conn, seller2Id, "Classic Hardcover Journal Notebook",
                    "Premium thick paper dotted journal notebook with pen holder and ribbon bookmarks.",
                    new BigDecimal("349.00"), 60, "Stationery",
                    "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=500&auto=format&fit=crop&q=60");

            long p8 = insertProduct(conn, seller1Id, "Compact USB-C Fast Charger 65W",
                    "GaN technology fast charger with dual USB-C and single USB-A ports for laptops and phones.",
                    new BigDecimal("1499.00"), 45, "Electronics",
                    "https://images.unsplash.com/photo-1622445262464-84b1456045b6?w=500&auto=format&fit=crop&q=60");

            // 3. Seed Completed Order for buyer1 (enabling reviews)
            long orderId = insertOrder(conn, buyer1Id, new BigDecimal("6298.00"),
                    OrderStatus.DELIVERED.name(), "123 Anna Salai, Guindy, Chennai, Tamil Nadu - 600025");

            insertOrderItem(conn, orderId, p2, 1, new BigDecimal("4999.00"));
            insertOrderItem(conn, orderId, p4, 1, new BigDecimal("1299.00"));

            // 4. Seed Reviews for delivered products
            insertReview(conn, buyer1Id, p2, orderId, 5,
                    "Incredible sound quality and outstanding battery life! The noise cancellation is perfect for studying.");
            insertReview(conn, buyer1Id, p4, orderId, 5,
                    "Must read for every computer science engineer. Arrived in pristine condition.");

            LOGGER.info("Seed data population completed successfully.");
        } catch (Exception e) {
            LOGGER.error("Failed to populate seed data", e);
        }
    }

    private long insertUser(Connection conn, String name, String email, String passwordHash, String role) throws Exception {
        String sql = "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalStateException("Failed to insert user: " + email);
    }

    private long insertProduct(Connection conn, long sellerId, String name, String desc,
                               BigDecimal price, int stock, String category, String img) throws Exception {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_quantity, category, image_url, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, sellerId);
            ps.setString(2, name);
            ps.setString(3, desc);
            ps.setBigDecimal(4, price);
            ps.setInt(5, stock);
            ps.setString(6, category);
            ps.setString(7, img);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalStateException("Failed to insert product: " + name);
    }

    private long insertOrder(Connection conn, long buyerId, BigDecimal total, String status, String address) throws Exception {
        String sql = "INSERT INTO orders (buyer_id, total_amount, status, shipping_address, payment_method) VALUES (?, ?, ?, ?, 'MOCK_CARD')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, buyerId);
            ps.setBigDecimal(2, total);
            ps.setString(3, status);
            ps.setString(4, address);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new IllegalStateException("Failed to insert order");
    }

    private void insertOrderItem(Connection conn, long orderId, long productId, int qty, BigDecimal price) throws Exception {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, productId);
            ps.setInt(3, qty);
            ps.setBigDecimal(4, price);
            ps.executeUpdate();
        }
    }

    private void insertReview(Connection conn, long buyerId, long productId, long orderId, int rating, String comment) throws Exception {
        String sql = "INSERT INTO reviews (buyer_id, product_id, order_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            ps.setLong(3, orderId);
            ps.setInt(4, rating);
            ps.setString(5, comment);
            ps.executeUpdate();
        }
    }
}
