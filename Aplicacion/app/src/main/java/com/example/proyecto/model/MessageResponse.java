package com.example.proyecto.model;

public class MessageResponse {
    private Long id;
    private String content;
    private Long chatId;
    private Long senderId;
    private String senderUsername;
    private String sentAt;

    public Long getId() { return id; }
    public String getContent() { return content; }
    public Long getChatId() { return chatId; }
    public Long getSenderId() { return senderId; }
    public String getSenderUsername() { return senderUsername; }
    public String getSentAt() { return sentAt; }
}