package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.OrderDTO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.model.OrderStatus;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderController", urlPatterns = {
        "/orders",
        "/orders/detail",
        "/seller/orders",
        "/seller/orders/status"
})
public class OrderController extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() {
        orderService = (OrderService) getServletContext().getAttribute(DBContextListener.ATTR_ORDER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        UserResponseDTO user = getLoggedInUser(req);

        if ("/seller/orders".equals(path)) {
            List<OrderDTO> sellerOrders = orderService.getOrdersBySeller(user.getId());
            req.setAttribute("orders", sellerOrders);
            req.getRequestDispatcher("/WEB-INF/views/seller/orders.jsp").forward(req, resp);
        } else {
            // Buyer order history
            List<OrderDTO> buyerOrders = orderService.getOrdersByBuyer(user.getId());
            req.setAttribute("orders", buyerOrders);
            req.getRequestDispatcher("/WEB-INF/views/orders/history.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        UserResponseDTO user = getLoggedInUser(req);

        if ("/seller/orders/status".equals(path)) {
            String orderIdStr = req.getParameter("orderId");
            String statusStr = req.getParameter("status");

            try {
                Long orderId = Long.parseLong(orderIdStr);
                OrderStatus status = OrderStatus.fromString(statusStr);
                orderService.updateOrderStatus(orderId, status);

                if (user.getRole() == Role.ADMIN) {
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard?statusUpdated=true");
                } else {
                    resp.sendRedirect(req.getContextPath() + "/seller/orders?statusUpdated=true");
                }
            } catch (AppException e) {
                resp.sendRedirect(req.getContextPath() + "/seller/orders?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/seller/orders");
            }
        }
    }

    private UserResponseDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
    }
}
