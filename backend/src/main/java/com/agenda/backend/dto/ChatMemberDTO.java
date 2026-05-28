package com.agenda.backend.dto;

import java.time.LocalDateTime;

public class ChatMemberDTO {

    private Long userId;
    private String username;
    private LocalDateTime joinedAt;

    public ChatMemberDTO() {
    }

    public ChatMemberDTO(Long userId, String username, LocalDateTime joinedAt) {
        this.userId = userId;
        this.username = username;
        this.joinedAt = joinedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}