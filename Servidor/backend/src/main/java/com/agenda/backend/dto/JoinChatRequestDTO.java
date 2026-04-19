package com.agenda.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinChatRequestDTO {

    @NotBlank(message = "El código del chat no puede estar vacío")
    private String joinCode;

    public JoinChatRequestDTO() {
    }

    public JoinChatRequestDTO(String joinCode) {
        this.joinCode = joinCode;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }
}