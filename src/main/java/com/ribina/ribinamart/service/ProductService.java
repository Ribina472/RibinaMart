package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dao.ReviewDAO;
import com.ribina.ribinamart.dto.ProductDTO;
import com.ribina.ribinamart.exception.AuthorizationException;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductService {

    private final ProductDAO productDAO;
    private final ReviewDAO reviewDAO;

    public ProductService(ProductDAO productDAO, ReviewDAO reviewDAO) {
        this.productDAO = productDAO;
        this.reviewDAO = reviewDAO;
    }

    public ProductDTO createProduct(Long sellerId, String name, String description,
                                    BigDecimal price, int stockQuantity, String category, String imageUrl) {
        validateProductInput(name, price, stockQuantity, category);

        if (sellerId == null || sellerId <= 0) {
            throw new ValidationException("Invalid seller ID.");
        }

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(name.trim());
        product.setDescription(description != null ? description.trim() : "");
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category.trim());
        product.setImageUrl(imageUrl != null && !imageUrl.trim().isEmpty() ? imageUrl.trim() : "https://via.placeholder.com/300?text=Product");
        product.setStatus(ProductStatus.ACTIVE);

        try {
            Product saved = productDAO.save(product);
            return ProductDTO.fromEntity(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Database error creating product listing", e);
        }
    }

    public ProductDTO updateProduct(Long sellerId, Long productId, String name, String description,
                                    BigDecimal price, int stockQuantity, String category, String imageUrl) {
        validateProductInput(name, price, stockQuantity, category);

        try {
            Product existing = productDAO.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (!existing.getSellerId().equals(sellerId)) {
                throw new AuthorizationException("You do not have permission to update this product listing.");
            }

            existing.setName(name.trim());
            existing.setDescription(description != null ? description.trim() : "");
            existing.setPrice(price);
            existing.setStockQuantity(stockQuantity);
            existing.setCategory(category.trim());
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                existing.setImageUrl(imageUrl.trim());
            }

            productDAO.update(existing);
            return ProductDTO.fromEntity(existing);
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating product", e);
        }
    }

    public void deleteProduct(Long sellerId, Long productId) {
        try {
            Product existing = productDAO.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (!existing.getSellerId().equals(sellerId)) {
                throw new AuthorizationException("You do not have permission to delete this product listing.");
            }

            productDAO.delete(productId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error deleting product", e);
        }
    }

    public void adminModerateProduct(Long productId, ProductStatus newStatus) {
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID.");
        }
        if (newStatus == null) {
            throw new ValidationException("Invalid product status.");
        }
        try {
            boolean updated = productDAO.updateStatus(productId, newStatus);
            if (!updated) {
                throw new ResourceNotFoundException("Product not found with ID: " + productId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error moderating product", e);
        }
    }

    public ProductDTO getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid product ID.");
        }
        try {
            Product product = productDAO.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

            ProductDTO dto = ProductDTO.fromEntity(product);
            if (reviewDAO != null) {
                dto.setAverageRating(reviewDAO.getAverageRating(id));
                dto.setReviewCount(reviewDAO.getReviewCount(id));
            }
            return dto;
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching product details", e);
        }
    }

    public List<ProductDTO> getAllActiveProducts() {
        try {
            List<Product> products = productDAO.findAllActive();
            return enrichWithRatings(products);
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching active products", e);
        }
    }

    public List<ProductDTO> getProductsBySeller(Long sellerId) {
        if (sellerId == null || sellerId <= 0) {
            throw new ValidationException("Invalid seller ID.");
        }
        try {
            List<Product> products = productDAO.findBySellerId(sellerId);
            return enrichWithRatings(products);
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching seller products", e);
        }
    }

    public List<ProductDTO> searchAndFilter(String category, String keyword) {
        try {
            List<Product> products = productDAO.searchAndFilter(category, keyword);
            return enrichWithRatings(products);
        } catch (SQLException e) {
            throw new RuntimeException("Database error searching products", e);
        }
    }

    public List<String> getAllCategories() {
        try {
            return productDAO.findAllCategories();
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching categories", e);
        }
    }

    public List<ProductDTO> getAllForAdmin() {
        try {
            List<Product> products = productDAO.findAllForAdmin();
            return enrichWithRatings(products);
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching admin products", e);
        }
    }

    private List<ProductDTO> enrichWithRatings(List<Product> products) throws SQLException {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : products) {
            ProductDTO dto = ProductDTO.fromEntity(p);
            if (reviewDAO != null) {
                dto.setAverageRating(reviewDAO.getAverageRating(p.getId()));
                dto.setReviewCount(reviewDAO.getReviewCount(p.getId()));
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private void validateProductInput(String name, BigDecimal price, int stockQuantity, String category) {
        Map<String, String> errors = new HashMap<>();

        if (!ValidationUtil.isNotEmpty(name) || name.trim().length() < 2 || name.trim().length() > 200) {
            errors.put("name", "Product name must be between 2 and 200 characters.");
        }

        if (!ValidationUtil.isValidPrice(price)) {
            errors.put("price", "Product price must be greater than 0.00.");
        }

        if (!ValidationUtil.isValidStock(stockQuantity)) {
            errors.put("stockQuantity", "Stock quantity cannot be negative.");
        }

        if (!ValidationUtil.isNotEmpty(category)) {
            errors.put("category", "Product category is required.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Product validation failed", errors);
        }
    }
}
