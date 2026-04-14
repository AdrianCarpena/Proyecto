package com.agenda.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendMessageRequestDTO {

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 600, message = "El mensaje no puede tener más de 600 caracteres")
    private String content;

    public SendMessageRequestDTO() {
    }

    public SendMessageRequestDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}