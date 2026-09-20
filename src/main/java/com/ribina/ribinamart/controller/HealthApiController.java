package com.ribina.ribinamart.controller;

import com.ribina.ribinamart.util.DBConnectionPool;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Health check endpoint required for deployment verification (Week 8).
 * Returns HTTP 200 with {"status":"UP","db":"UP"}.
 */
@WebServlet(name = "HealthApiController", urlPatterns = {"/api/v1/health"})
public class HealthApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        boolean dbUp = DBConnectionPool.isHealthy();
        if (dbUp) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"status\":\"UP\",\"db\":\"UP\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            resp.getWriter().write("{\"status\":\"DOWN\",\"db\":\"DOWN\"}");
        }
    }
}
