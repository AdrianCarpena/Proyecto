package com.agenda.backend.dto;

import java.time.LocalDateTime;

public class MessageResponseDTO {

    private Long id;
    private String content;
    private Long chatId;
    private Long senderId;
    private String senderUsername;
    private LocalDateTime sentAt;

    public MessageResponseDTO() {
    }

    public MessageResponseDTO(Long id, String content, Long chatId, Long senderId,String senderUsername, LocalDateTime sentAt) {
        this.id = id;
        this.content = content;
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}