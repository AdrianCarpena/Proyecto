package com.example.proyecto.model;

public class SendMessageRequest {
    private String content;
    public SendMessageRequest(String content) { this.content = content; }
    public String getContent() { return content; }
}