package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    Product save(Product product) throws SQLException;
    Optional<Product> findById(Long id) throws SQLException;
    List<Product> findAllActive() throws SQLException;
    List<Product> findBySellerId(Long sellerId) throws SQLException;
    List<Product> searchAndFilter(String category, String keyword) throws SQLException;
    List<String> findAllCategories() throws SQLException;
    boolean update(Product product) throws SQLException;
    boolean delete(Long id) throws SQLException;
    boolean updateStatus(Long id, ProductStatus status) throws SQLException;
    boolean updateStock(Long productId, int quantityDelta, Connection conn) throws SQLException;
    List<Product> findAllForAdmin() throws SQLException;
}
