package com.agenda.backend.dto;

public class ChatJoinCodeDTO {
    private Long chatId;
    private String joinCode;

    public ChatJoinCodeDTO(Long chatId, String joinCode) {
        this.chatId = chatId;
        this.joinCode = joinCode;
    }

    public Long getChatId() { return chatId; }
    public String getJoinCode() { return joinCode; }
}