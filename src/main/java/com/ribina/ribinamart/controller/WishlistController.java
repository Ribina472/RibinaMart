package com.ribina.ribinamart.controller;

import com.google.gson.Gson;
import com.ribina.ribinamart.dto.ApiResponse;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.model.WishlistItem;
import com.ribina.ribinamart.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Controller handling buyer wishlist (Save-for-later) interactions.
 * Maps to /wishlist, /wishlist/add, /wishlist/remove.
 */
@WebServlet(name = "WishlistController", urlPatterns = {
        "/wishlist",
        "/wishlist/add",
        "/wishlist/remove"
})
public class WishlistController extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishlistController.class);
    private final Gson gson = new Gson();
    private WishlistService wishlistService;

    @Override
    public void init() {
        wishlistService = (WishlistService) getServletContext().getAttribute("wishlistService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserResponseDTO user = getLoggedInUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login?redirect=" +
                    URLEncoder.encode(req.getRequestURI(), StandardCharsets.UTF_8));
            return;
        }

        List<WishlistItem> items = wishlistService.getWishlist(user.getId());
        req.setAttribute("wishlistItems", items);
        req.getRequestDispatcher("/WEB-INF/views/buyer/wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        UserResponseDTO user = getLoggedInUser(req);

        if (user == null) {
            if (isAjax(req)) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(ApiResponse.error("Please login to save items to your wishlist.")));
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        if ("/wishlist/add".equals(path)) {
            handleAdd(req, resp, user);
        } else if ("/wishlist/remove".equals(path)) {
            handleRemove(req, resp, user);
        }
    }

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO user) throws IOException {
        String productIdStr = req.getParameter("productId");
        boolean isAjax = isAjax(req);

        try {
            Long productId = Long.parseLong(productIdStr);
            boolean added = wishlistService.addToWishlist(user.getId(), productId);

            if (isAjax) {
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(ApiResponse.ok(added ? "Item added to your wishlist!" : "Item is already in your wishlist.")));
                return;
            }

            String referer = req.getHeader("Referer");
            if (referer != null && !referer.isEmpty()) {
                resp.sendRedirect(referer + (referer.contains("?") ? "&" : "?") + "wishlistSuccess=true");
            } else {
                resp.sendRedirect(req.getContextPath() + "/wishlist?success=true");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to add product to wishlist: {}", e.getMessage());
            if (isAjax) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.getWriter().write(gson.toJson(ApiResponse.error(e.getMessage())));
            } else {
                resp.sendRedirect(req.getContextPath() + "/wishlist?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
            }
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp, UserResponseDTO user) throws IOException {
        String productIdStr = req.getParameter("productId");
        try {
            Long productId = Long.parseLong(productIdStr);
            wishlistService.removeFromWishlist(user.getId(), productId);
            resp.sendRedirect(req.getContextPath() + "/wishlist?removed=true");
        } catch (Exception e) {
            LOGGER.error("Failed to remove product from wishlist: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/wishlist");
        }
    }

    private boolean isAjax(HttpServletRequest req) {
        String requestedWith = req.getHeader("X-Requested-With");
        String format = req.getParameter("format");
        return "XMLHttpRequest".equalsIgnoreCase(requestedWith) || "json".equalsIgnoreCase(format);
    }

    private UserResponseDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
    }
}
