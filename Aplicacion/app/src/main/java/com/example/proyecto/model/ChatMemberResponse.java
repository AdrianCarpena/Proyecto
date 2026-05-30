package com.example.proyecto.model;

public class ChatMemberResponse {

    private Long userId;
    private String username;
    private String joinedAt;

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getJoinedAt() {
        return joinedAt;
    }
}