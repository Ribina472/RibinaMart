package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AuthController", urlPatterns = {"/auth/login", "/auth/register", "/auth/logout"})
public class AuthController extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = (UserService) getServletContext().getAttribute(DBContextListener.ATTR_USER_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/auth/login".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        } else if ("/auth/register".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } else if ("/auth/logout".equals(path)) {
            handleLogout(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/auth/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/auth/register".equals(path)) {
            handleRegister(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String redirect = req.getParameter("redirect");

        try {
            UserResponseDTO user = userService.login(email, password);

            // Session Fixation Protection: invalidate old session & regenerate session ID
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession newSession = req.getSession(true);
            newSession.setMaxInactiveInterval(1800); // 30 minutes
            newSession.setAttribute(AuthFilter.SESSION_USER, user);

            if (redirect != null && !redirect.trim().isEmpty() && !redirect.contains("://")) {
                resp.sendRedirect(req.getContextPath() + redirect);
                return;
            }

            // Redirect based on role
            if (user.getRole() == Role.ADMIN) {
                resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            } else if (user.getRole() == Role.SELLER) {
                resp.sendRedirect(req.getContextPath() + "/seller/dashboard");
            } else {
                resp.sendRedirect(req.getContextPath() + "/products");
            }
        } catch (AppException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("email", email);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String roleStr = req.getParameter("role");

        Role role = Role.fromString(roleStr);

        try {
            userService.register(name, email, password, role);
            resp.sendRedirect(req.getContextPath() + "/auth/login?registered=true");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("fieldErrors", e.getErrors());
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("role", roleStr);
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (AppException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/auth/login?loggedOut=true");
    }
}
