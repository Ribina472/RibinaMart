package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Order;
import com.ribina.ribinamart.model.OrderItem;
import com.ribina.ribinamart.model.OrderStatus;
import com.ribina.ribinamart.util.DBConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAOImpl implements OrderDAO {

    private static final String SQL_INSERT_ORDER =
            "INSERT INTO orders (buyer_id, total_amount, status, shipping_address, payment_method) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_ORDER_ITEM =
            "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) " +
            "VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_STOCK =
            "UPDATE products SET stock_quantity = stock_quantity - ? " +
            "WHERE id = ? AND stock_quantity >= ?";

    private static final String SQL_CLEAR_CART =
            "DELETE FROM cart_items WHERE user_id = ?";

    private static final String SQL_FIND_ORDER_BY_ID =
            "SELECT o.id, o.buyer_id, u.name AS buyer_name, u.email AS buyer_email, " +
            "o.total_amount, o.status, o.shipping_address, o.payment_method, o.created_at " +
            "FROM orders o JOIN users u ON o.buyer_id = u.id WHERE o.id = ?";

    private static final String SQL_FIND_ITEMS_BY_ORDER =
            "SELECT oi.id, oi.order_id, oi.product_id, oi.quantity, oi.price_at_purchase, oi.created_at, " +
            "p.name AS product_name, p.image_url, p.seller_id " +
            "FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";

    private static final String SQL_FIND_BY_BUYER =
            "SELECT o.id, o.buyer_id, u.name AS buyer_name, u.email AS buyer_email, " +
            "o.total_amount, o.status, o.shipping_address, o.payment_method, o.created_at " +
            "FROM orders o JOIN users u ON o.buyer_id = u.id WHERE o.buyer_id = ? ORDER BY o.created_at DESC";

    private static final String SQL_FIND_BY_SELLER =
            "SELECT DISTINCT o.id, o.buyer_id, u.name AS buyer_name, u.email AS buyer_email, " +
            "o.total_amount, o.status, o.shipping_address, o.payment_method, o.created_at " +
            "FROM orders o " +
            "JOIN users u ON o.buyer_id = u.id " +
            "JOIN order_items oi ON o.id = oi.order_id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE p.seller_id = ? ORDER BY o.created_at DESC";

    private static final String SQL_FIND_ALL =
            "SELECT o.id, o.buyer_id, u.name AS buyer_name, u.email AS buyer_email, " +
            "o.total_amount, o.status, o.shipping_address, o.payment_method, o.created_at " +
            "FROM orders o JOIN users u ON o.buyer_id = u.id ORDER BY o.created_at DESC";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE orders SET status = ? WHERE id = ?";

    @Override
    public Order createOrderWithItems(Order order, List<OrderItem> items) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection()) {
            boolean origAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                // 1. Insert order
                try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, order.getBuyerId());
                    ps.setBigDecimal(2, order.getTotalAmount());
                    ps.setString(3, order.getStatus().name());
                    ps.setString(4, order.getShippingAddress());
                    ps.setString(5, order.getPaymentMethod());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            order.setId(rs.getLong(1));
                        }
                    }
                }

                // 2. Decrement stock and insert order items
                try (PreparedStatement psItem = conn.prepareStatement(SQL_INSERT_ORDER_ITEM, Statement.RETURN_GENERATED_KEYS);
                     PreparedStatement psStock = conn.prepareStatement(SQL_UPDATE_STOCK)) {

                    for (OrderItem item : items) {
                        // Check and decrement stock atomically
                        psStock.setInt(1, item.getQuantity());
                        psStock.setLong(2, item.getProductId());
                        psStock.setInt(3, item.getQuantity());
                        int affected = psStock.executeUpdate();
                        if (affected == 0) {
                            throw new ValidationException("Product with ID " + item.getProductId() + " is out of stock or has insufficient quantity.");
                        }

                        // Insert item
                        psItem.setLong(1, order.getId());
                        psItem.setLong(2, item.getProductId());
                        psItem.setInt(3, item.getQuantity());
                        psItem.setBigDecimal(4, item.getPriceAtPurchase());
                        psItem.executeUpdate();

                        try (ResultSet rs = psItem.getGeneratedKeys()) {
                            if (rs.next()) {
                                item.setId(rs.getLong(1));
                            }
                        }
                        item.setOrderId(order.getId());
                    }
                }

                // 3. Clear the buyer's cart
                try (PreparedStatement psClear = conn.prepareStatement(SQL_CLEAR_CART)) {
                    psClear.setLong(1, order.getBuyerId());
                    psClear.executeUpdate();
                }

                conn.commit();
                order.setItems(items);
                return order;
            } catch (Exception ex) {
                conn.rollback();
                if (ex instanceof SQLException) {
                    throw (SQLException) ex;
                } else if (ex instanceof RuntimeException) {
                    throw (RuntimeException) ex;
                }
                throw new SQLException("Failed to create order transaction", ex);
            } finally {
                conn.setAutoCommit(origAutoCommit);
            }
        }
    }

    @Override
    public Optional<Order> findById(Long id) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ORDER_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsByOrderId(order.getId(), conn));
                    return Optional.of(order);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findByBuyerId(Long buyerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_BUYER)) {
            ps.setLong(1, buyerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    order.setItems(findItemsByOrderId(order.getId(), conn));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> findBySellerId(Long sellerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_SELLER)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrderRow(rs);
                    // Filter items to show only products belonging to this seller
                    List<OrderItem> allItems = findItemsByOrderId(order.getId(), conn);
                    List<OrderItem> sellerItems = new ArrayList<>();
                    for (OrderItem item : allItems) {
                        if (sellerId.equals(item.getSellerId())) {
                            sellerItems.add(item);
                        }
                    }
                    order.setItems(sellerItems);
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> findAll() throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = mapOrderRow(rs);
                order.setItems(findItemsByOrderId(order.getId(), conn));
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public boolean updateStatus(Long orderId, OrderStatus status) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.name());
            ps.setLong(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    private List<OrderItem> findItemsByOrderId(Long orderId, Connection conn) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_FIND_ITEMS_BY_ORDER)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getLong("id"));
                    item.setOrderId(rs.getLong("order_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImageUrl(rs.getString("image_url"));
                    item.setSellerId(rs.getLong("seller_id"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getLong("id"));
        o.setBuyerId(rs.getLong("buyer_id"));
        o.setBuyerName(rs.getString("buyer_name"));
        o.setBuyerEmail(rs.getString("buyer_email"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(OrderStatus.fromString(rs.getString("status")));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
