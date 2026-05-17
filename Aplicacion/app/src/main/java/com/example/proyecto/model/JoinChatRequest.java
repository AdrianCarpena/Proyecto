package com.example.proyecto.model;

public class JoinChatRequest {
    private String joinCode;
    public JoinChatRequest(String joinCode) { this.joinCode = joinCode; }
    public String getJoinCode() { return joinCode; }
}