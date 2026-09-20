package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.filter.AuthFilter;
import com.ribina.ribinamart.listener.DBContextListener;
import com.ribina.ribinamart.service.ReviewService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ReviewController", urlPatterns = {"/reviews/add"})
public class ReviewController extends HttpServlet {

    private ReviewService reviewService;

    @Override
    public void init() {
        reviewService = (ReviewService) getServletContext().getAttribute(DBContextListener.ATTR_REVIEW_SERVICE);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserResponseDTO user = getLoggedInUser(req);
        String productIdStr = req.getParameter("productId");
        String ratingStr = req.getParameter("rating");
        String comment = req.getParameter("comment");

        try {
            Long productId = Long.parseLong(productIdStr);
            int rating = Integer.parseInt(ratingStr);

            reviewService.addReview(user.getId(), productId, rating, comment);
            resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productId + "&reviewed=true");
        } catch (AppException e) {
            resp.sendRedirect(req.getContextPath() + "/products/detail?id=" + productIdStr + "&error=" +
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/products");
        }
    }

    private UserResponseDTO getLoggedInUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (UserResponseDTO) session.getAttribute(AuthFilter.SESSION_USER) : null;
    }
}
