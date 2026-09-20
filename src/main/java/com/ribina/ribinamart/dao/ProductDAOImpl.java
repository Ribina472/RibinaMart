package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.util.DBConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAOImpl implements ProductDAO {

    private static final String BASE_SELECT =
            "SELECT p.id, p.seller_id, u.name AS seller_name, p.name, p.description, p.price, " +
            "p.stock_quantity, p.category, p.image_url, p.status, p.created_at " +
            "FROM products p JOIN users u ON p.seller_id = u.id ";

    private static final String SQL_INSERT =
            "INSERT INTO products (seller_id, name, description, price, stock_quantity, category, image_url, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_ID =
            BASE_SELECT + "WHERE p.id = ?";

    private static final String SQL_FIND_ALL_ACTIVE =
            BASE_SELECT + "WHERE p.status = 'ACTIVE' ORDER BY p.created_at DESC";

    private static final String SQL_FIND_BY_SELLER =
            BASE_SELECT + "WHERE p.seller_id = ? AND p.status != 'INACTIVE' ORDER BY p.created_at DESC";

    private static final String SQL_FIND_ALL_ADMIN =
            BASE_SELECT + "ORDER BY p.created_at DESC";

    private static final String SQL_CATEGORIES =
            "SELECT DISTINCT category FROM products WHERE status = 'ACTIVE' ORDER BY category ASC";

    private static final String SQL_UPDATE =
            "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ?, " +
            "category = ?, image_url = ?, status = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "UPDATE products SET status = 'INACTIVE' WHERE id = ?";

    private static final String SQL_UPDATE_STATUS =
            "UPDATE products SET status = ? WHERE id = ?";

    private static final String SQL_UPDATE_STOCK =
            "UPDATE products SET stock_quantity = stock_quantity + ? WHERE id = ? AND (stock_quantity + ?) >= 0";

    @Override
    public Product save(Product product) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, product.getSellerId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            ps.setInt(5, product.getStockQuantity());
            ps.setString(6, product.getCategory());
            ps.setString(7, product.getImageUrl());
            ProductStatus status = product.getStatus() != null ? product.getStatus() : ProductStatus.ACTIVE;
            ps.setString(8, status.name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                }
            }
            return product;
        }
    }

    @Override
    public Optional<Product> findById(Long id) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAllActive() throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_ACTIVE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    @Override
    public List<Product> findBySellerId(Long sellerId) throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_SELLER)) {
            ps.setLong(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        }
        return products;
    }

    @Override
    public List<Product> searchAndFilter(String category, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE p.status = 'ACTIVE' ");

        boolean hasCategory = category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category.trim());
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (hasCategory) {
            sql.append("AND LOWER(p.category) = ? ");
        }
        if (hasKeyword) {
            sql.append("AND (LOWER(p.name) LIKE ? OR LOWER(p.description) LIKE ?) ");
        }
        sql.append("ORDER BY p.created_at DESC");

        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (hasCategory) {
                ps.setString(paramIndex++, category.trim().toLowerCase());
            }
            if (hasKeyword) {
                String kwParam = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, kwParam);
                ps.setString(paramIndex++, kwParam);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        }
        return products;
    }

    @Override
    public List<String> findAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CATEGORIES);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(rs.getString(1));
            }
        }
        return categories;
    }

    @Override
    public boolean update(Product product) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setInt(4, product.getStockQuantity());
            ps.setString(5, product.getCategory());
            ps.setString(6, product.getImageUrl());
            ps.setString(7, product.getStatus().name());
            ps.setLong(8, product.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long id) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(Long id, ProductStatus status) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATUS)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStock(Long productId, int quantityDelta, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STOCK)) {
            ps.setInt(1, quantityDelta);
            ps.setLong(2, productId);
            ps.setInt(3, quantityDelta);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Product> findAllForAdmin() throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_ADMIN);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setSellerName(rs.getString("seller_name"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        p.setCategory(rs.getString("category"));
        p.setImageUrl(rs.getString("image_url"));
        p.setStatus(ProductStatus.fromString(rs.getString("status")));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
