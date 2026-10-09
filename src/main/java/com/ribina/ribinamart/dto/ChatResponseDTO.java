package com.ribina.ribinamart.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object encapsulating Chatbot query responses.
 */
public class ChatResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reply;
    private String provider;
    private String timestamp;

    public ChatResponseDTO() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public ChatResponseDTO(String reply, String provider) {
        this.reply = reply;
        this.provider = provider;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
