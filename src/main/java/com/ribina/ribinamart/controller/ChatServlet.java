package com.ribina.ribinamart.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ribina.ribinamart.chatbot.ChatService;
import com.ribina.ribinamart.dto.ApiResponse;
import com.ribina.ribinamart.dto.ChatResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Controller endpoint exposing the AI Chatbot API.
 * Supports both JSON body and standard Form POST queries.
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/api/chat", "/api/v1/chat"})
public class ChatServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServlet.class);
    private final Gson gson = new Gson();
    private ChatService chatService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.chatService = (ChatService) config.getServletContext().getAttribute("chatService");
        if (this.chatService == null) {
            this.chatService = new ChatService();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ChatResponseDTO greeting = new ChatResponseDTO(
                "Hello! Welcome to RibinaMart AI Shopping Assistant. How may I help you today?",
                "System"
        );
        resp.getWriter().write(gson.toJson(ApiResponse.ok(greeting)));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String userMessage = null;

        // Try reading JSON payload first
        String contentType = req.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("application/json")) {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            try {
                JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();
                if (json.has("message")) {
                    userMessage = json.get("message").getAsString();
                }
            } catch (Exception e) {
                LOGGER.debug("Could not parse JSON body: {}", e.getMessage());
            }
        }

        // Fallback to form parameter if JSON body was not provided
        if (userMessage == null || userMessage.trim().isEmpty()) {
            userMessage = req.getParameter("message");
        }

        // Resolve session identifier for rate limiting & continuity
        HttpSession session = req.getSession(true);
        String sessionId = session != null ? session.getId() : req.getRemoteAddr();

        ChatService activeService = this.chatService;
        if (activeService == null) {
            activeService = (ChatService) getServletContext().getAttribute("chatService");
            if (activeService == null) {
                activeService = new ChatService();
            }
        }

        ChatResponseDTO responseDTO = activeService.processMessage(userMessage, sessionId);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(ApiResponse.ok(responseDTO)));
    }
}
