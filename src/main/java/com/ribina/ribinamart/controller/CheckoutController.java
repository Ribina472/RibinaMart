package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.CartSummaryDTO;
import com.ribina.ribinamart.dto.OrderDTO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.service.CartService;
import com.ribina.ribinamart.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init() {
        cartService = (CartService) getServletContext().getAttribute(DBContextListener.ATTR_CART_SERVICE);
        orderService = (OrderService) getServletContext().getAttribute(DBContextListener.ATTR_ORDER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO user = getLoggedInUser(req);
        CartSummaryDTO cart = cartService.getCartSummary(user.getId());

        if (cart.getItems().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=empty");
            return;
        }

        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/checkout/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO user = getLoggedInUser(req);
        String address = req.getParameter("shippingAddress");
        String paymentMethod = req.getParameter("paymentMethod");

        try {
            OrderDTO order = orderService.checkout(user.getId(), address, paymentMethod);
            resp.sendRedirect(req.getContextPath() + "/orders?placed=true&orderId=" + order.getId());
        } catch (AppException e) {
            CartSummaryDTO cart = cartService.getCartSummary(user.getId());
            req.setAttribute("cart", cart);
            req.setAttribute("shippingAddress", address);
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/checkout/checkout.jsp").forward(req, resp);
        }
    }

    private UserResponseDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
    }
}
