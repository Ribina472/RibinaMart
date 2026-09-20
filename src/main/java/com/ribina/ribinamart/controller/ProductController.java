package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.ProductDTO;
import com.ribina.ribinamart.dto.ReviewDTO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.service.ProductService;
import com.ribina.ribinamart.service.ReviewService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductController", urlPatterns = {"", "/home", "/products", "/products/detail"})
public class ProductController extends HttpServlet {

    private ProductService productService;
    private ReviewService reviewService;

    @Override
    public void init() {
        productService = (ProductService) getServletContext().getAttribute(DBContextListener.ATTR_PRODUCT_SERVICE);
        reviewService = (ReviewService) getServletContext().getAttribute(DBContextListener.ATTR_REVIEW_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/products/detail".equals(path)) {
            handleProductDetail(req, resp);
        } else {
            handleProductList(req, resp);
        }
    }

    private void handleProductList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String category = req.getParameter("category");
        String keyword = req.getParameter("keyword");

        List<ProductDTO> products;
        if ((category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) ||
                (keyword != null && !keyword.trim().isEmpty())) {
            products = productService.searchAndFilter(category, keyword);
        } else {
            products = productService.getAllActiveProducts();
        }

        List<String> categories = productService.getAllCategories();

        req.setAttribute("products", products);
        req.setAttribute("categories", categories);
        req.setAttribute("selectedCategory", category != null ? category : "ALL");
        req.setAttribute("keyword", keyword != null ? keyword : "");

        req.getRequestDispatcher("/WEB-INF/views/products/list.jsp").forward(req, resp);
    }

    private void handleProductDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        try {
            Long productId = Long.parseLong(idStr);
            ProductDTO product = productService.getProductById(productId);
            List<ReviewDTO> reviews = reviewService.getProductReviews(productId);

            HttpSession session = req.getSession(false);
            UserResponseDTO currentUser = (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
            boolean canReview = false;
            if (currentUser != null) {
                canReview = reviewService.canUserReview(currentUser.getId(), productId);
            }

            req.setAttribute("product", product);
            req.setAttribute("reviews", reviews);
            req.setAttribute("canReview", canReview);

            req.getRequestDispatcher("/WEB-INF/views/products/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }
}
