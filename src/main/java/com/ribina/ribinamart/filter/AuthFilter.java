package com.ribina.ribinamart.filter;

import com.ribina.ribinamart.dto.ApiResponse;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Authentication and authorization filter enforcing role-based access control.
 * Strictly adheres to Week 1, 4, 7 security rules.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    public static final String SESSION_USER = "currentUser";

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI().substring(contextPath.length());

        // Static resources and public routes pass through
        if (isPublicResource(uri)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        UserResponseDTO user = (session != null) ? (UserResponseDTO) session.getAttribute(SESSION_USER) : null;

        // Check Seller routes
        if (uri.startsWith("/seller/") || uri.equals("/seller")) {
            if (user == null) {
                handleUnauthorized(req, resp, uri);
                return;
            }
            if (user.getRole() != Role.SELLER && user.getRole() != Role.ADMIN) {
                handleForbidden(req, resp);
                return;
            }
        }

        // Check Admin routes
        if (uri.startsWith("/admin/") || uri.equals("/admin")) {
            if (user == null) {
                handleUnauthorized(req, resp, uri);
                return;
            }
            if (user.getRole() != Role.ADMIN) {
                handleForbidden(req, resp);
                return;
            }
        }

        // Check Buyer-protected routes
        if (isBuyerRoute(uri)) {
            if (user == null) {
                handleUnauthorized(req, resp, uri);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicResource(String uri) {
        return uri.equals("/") ||
                uri.equals("/home") ||
                uri.startsWith("/products") ||
                uri.startsWith("/auth/") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/api/v1/health") ||
                uri.startsWith("/error");
    }

    private boolean isBuyerRoute(String uri) {
        return uri.startsWith("/cart") ||
                uri.startsWith("/checkout") ||
                uri.startsWith("/orders") ||
                uri.startsWith("/reviews/add") ||
                uri.startsWith("/buyer/");
    }

    private void handleUnauthorized(HttpServletRequest req, HttpServletResponse resp, String originalUri) throws IOException {
        if (originalUri.startsWith("/api/")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("Authentication required. Please log in.")));
        } else {
            String encoded = URLEncoder.encode(originalUri, StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/auth/login?redirect=" + encoded);
        }
    }

    private void handleForbidden(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getRequestURI().contains("/api/")) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("application/json");
            resp.getWriter().write(JsonUtil.toJson(ApiResponse.error("Access forbidden: Insufficient permissions.")));
        } else {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this resource.");
        }
    }

    @Override
    public void destroy() {
    }
}
