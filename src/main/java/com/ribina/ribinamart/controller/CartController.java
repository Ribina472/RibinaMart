package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.CartSummaryDTO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.service.CartService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CartController", urlPatterns = {
        "/cart",
        "/cart/add",
        "/cart/update",
        "/cart/remove",
        "/cart/clear"
})
public class CartController extends HttpServlet {

    private CartService cartService;

    @Override
    public void init() {
        cartService = (CartService) getServletContext().getAttribute(DBContextListener.ATTR_CART_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO user = getLoggedInUser(req);
        CartSummaryDTO cart = cartService.getCartSummary(user.getId());
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("/WEB-INF/views/cart/view.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        UserResponseDTO user = getLoggedInUser(req);

        if ("/cart/add".equals(path)) {
            handleAdd(req, resp, user);
        } else if ("/cart/update".equals(path)) {
            handleUpdate(req, resp, user);
        } else if ("/cart/remove".equals(path)) {
            handleRemove(req, resp, user);
        } else if ("/cart/clear".equals(path)) {
            cartService.clearCart(user.getId());
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO user) throws IOException {
        String productIdStr = req.getParameter("productId");
        String qtyStr = req.getParameter("quantity");
        int qty = 1;
        try {
            if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                qty = Integer.parseInt(qtyStr);
            }
            Long productId = Long.parseLong(productIdStr);
            cartService.addToCart(user.getId(), productId, qty);
            resp.sendRedirect(req.getContextPath() + "/cart?added=true");
        } catch (AppException e) {
            resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productIdStr + "&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO user) throws IOException {
        String cartItemIdStr = req.getParameter("cartItemId");
        String qtyStr = req.getParameter("quantity");
        try {
            Long cartItemId = Long.parseLong(cartItemIdStr);
            int qty = Integer.parseInt(qtyStr);
            cartService.updateItemQuantity(user.getId(), cartItemId, qty);
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (AppException e) {
            resp.sendRedirect(req.getContextPath() + "/cart?error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO user) throws IOException {
        String cartItemIdStr = req.getParameter("cartItemId");
        try {
            Long cartItemId = Long.parseLong(cartItemIdStr);
            cartService.removeItem(user.getId(), cartItemId);
            resp.sendRedirect(req.getContextPath() + "/cart?removed=true");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private UserResponseDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
    }
}
