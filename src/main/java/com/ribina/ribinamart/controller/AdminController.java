package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.OrderDTO;
import com.ribina.ribinamart.dto.ProductDTO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.service.OrderService;
import com.ribina.ribinamart.service.ProductService;
import com.ribina.ribinamart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminController", urlPatterns = {
        "/admin/dashboard",
        "/admin/products/moderate"
})
public class AdminController extends HttpServlet {

    private UserService userService;
    private ProductService productService;
    private OrderService orderService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute(DBContextListener.ATTR_USER_SERVICE);
        productService = (ProductService) getServletContext().getAttribute(DBContextListener.ATTR_PRODUCT_SERVICE);
        orderService = (OrderService) getServletContext().getAttribute(DBContextListener.ATTR_ORDER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<UserResponseDTO> users = userService.getAllUsers();
        List<ProductDTO> products = productService.getAllForAdmin();
        List<OrderDTO> orders = orderService.getAllOrders();

        req.setAttribute("users", users);
        req.setAttribute("products", products);
        req.setAttribute("orders", orders);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();

        if ("/admin/products/moderate".equals(path)) {
            String productIdStr = req.getParameter("productId");
            String statusStr = req.getParameter("status");

            try {
                Long productId = Long.parseLong(productIdStr);
                ProductStatus status = ProductStatus.fromString(statusStr);
                productService.adminModerateProduct(productId, status);
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?moderated=true");
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard?error=failed");
            }
        }
    }
}
