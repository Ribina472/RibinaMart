package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.ProductDTO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "SellerProductController", urlPatterns = {
        "/seller/dashboard",
        "/seller/products",
        "/seller/products/new",
        "/seller/products/edit",
        "/seller/products/delete"
})
public class SellerProductController extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        productService = (ProductService) getServletContext().getAttribute(DBContextListener.ATTR_PRODUCT_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        UserResponseDTO seller = getLoggedInUser(req);

        if ("/seller/products/new".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } else if ("/seller/products/edit".equals(path)) {
            handleEditGet(req, resp, seller);
        } else {
            // Dashboard / products list
            List<ProductDTO> products = productService.getProductsBySeller(seller.getId());
            req.setAttribute("products", products);
            req.getRequestDispatcher("/WEB-INF/views/seller/dashboard.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        UserResponseDTO seller = getLoggedInUser(req);

        if ("/seller/products/new".equals(path)) {
            handleCreatePost(req, resp, seller);
        } else if ("/seller/products/edit".equals(path)) {
            handleEditPost(req, resp, seller);
        } else if ("/seller/products/delete".equals(path)) {
            handleDeletePost(req, resp, seller);
        }
    }

    private void handleEditGet(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO seller) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        try {
            Long productId = Long.parseLong(idStr);
            ProductDTO product = productService.getProductById(productId);
            req.setAttribute("product", product);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=notfound");
        }
    }

    private void handleCreatePost(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO seller) throws ServletException, IOException {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQuantity");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");

        try {
            BigDecimal price = new BigDecimal(priceStr);
            int stock = Integer.parseInt(stockStr);

            productService.createProduct(seller.getId(), name, description, price, stock, category, imageUrl);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?created=true");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getErrors());
            retainFormAttributes(req, name, description, priceStr, stockStr, category, imageUrl);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Invalid numeric value for price or stock quantity.");
            retainFormAttributes(req, name, description, priceStr, stockStr, category, imageUrl);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        }
    }

    private void handleEditPost(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO seller) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String priceStr = req.getParameter("price");
        String stockStr = req.getParameter("stockQuantity");
        String category = req.getParameter("category");
        String imageUrl = req.getParameter("imageUrl");

        try {
            Long productId = Long.parseLong(idStr);
            BigDecimal price = new BigDecimal(priceStr);
            int stock = Integer.parseInt(stockStr);

            productService.updateProduct(seller.getId(), productId, name, description, price, stock, category, imageUrl);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?updated=true");
        } catch (AppException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("isEdit", true);
            retainFormAttributes(req, name, description, priceStr, stockStr, category, imageUrl);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Invalid numeric values provided.");
            req.setAttribute("isEdit", true);
            retainFormAttributes(req, name, description, priceStr, stockStr, category, imageUrl);
            req.getRequestDispatcher("/WEB-INF/views/seller/product-form.jsp").forward(req, resp);
        }
    }

    private void handleDeletePost(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO seller) throws IOException {
        String idStr = req.getParameter("id");
        try {
            Long productId = Long.parseLong(idStr);
            productService.deleteProduct(seller.getId(), productId);
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?deleted=true");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/seller/dashboard?error=deletefailed");
        }
    }

    private UserResponseDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
    }

    private void retainFormAttributes(HttpServletRequest req, String name, String desc, String price, String stock, String cat, String img) {
        req.setAttribute("name", name);
        req.setAttribute("description", desc);
        req.setAttribute("price", price);
        req.setAttribute("stockQuantity", stock);
        req.setAttribute("category", cat);
        req.setAttribute("imageUrl", img);
    }
}
