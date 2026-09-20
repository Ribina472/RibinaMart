package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDAOTest {

    private static UserDAO userDAO;
    private static ProductDAO productDAO;
    private Long sellerId;

    @BeforeAll
    public static void init() throws Exception {
        TestDBHelper.setupTestDatabase();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
    }

    @BeforeEach
    public void setup() throws Exception {
        TestDBHelper.clearTables();
        User seller = new User();
        seller.setName("Campus Electronics");
        seller.setEmail("seller@campus.edu");
        seller.setPasswordHash("hash123");
        seller.setRole(Role.SELLER);
        User saved = userDAO.save(seller);
        sellerId = saved.getId();
    }

    @Test
    public void testSaveAndFindById() throws Exception {
        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName("Wireless Mouse");
        p.setDescription("Ergonomic 2.4GHz mouse");
        p.setPrice(new BigDecimal("499.00"));
        p.setStockQuantity(20);
        p.setCategory("Electronics");
        p.setImageUrl("https://example.com/mouse.jpg");
        p.setStatus(ProductStatus.ACTIVE);

        Product saved = productDAO.save(p);
        assertNotNull(saved.getId());

        Optional<Product> found = productDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Wireless Mouse", found.get().getName());
        assertEquals("Electronics", found.get().getCategory());
        assertEquals("Campus Electronics", found.get().getSellerName());
    }

    @Test
    public void testSearchAndFilter() throws Exception {
        Product p1 = new Product(null, sellerId, "Java in Action", "Programming book", new BigDecimal("799.00"), 10, "Books", null, ProductStatus.ACTIVE, null);
        Product p2 = new Product(null, sellerId, "Noise Cancelling Headphones", "Audio gear", new BigDecimal("2999.00"), 5, "Electronics", null, ProductStatus.ACTIVE, null);
        productDAO.save(p1);
        productDAO.save(p2);

        List<Product> books = productDAO.searchAndFilter("Books", null);
        assertEquals(1, books.size());
        assertEquals("Java in Action", books.get(0).getName());

        List<Product> searched = productDAO.searchAndFilter(null, "Headphones");
        assertEquals(1, searched.size());
        assertEquals("Noise Cancelling Headphones", searched.get(0).getName());
    }
}
