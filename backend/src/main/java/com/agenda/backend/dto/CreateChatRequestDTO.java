package com.agenda.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateChatRequestDTO {

    @NotBlank(message = "El nombre del chat no puede estar vacío")
    @Size(max = 15, message = "El nombre del chat no puede tener más de 15 caracteres")
    private String nombre;

    public CreateChatRequestDTO() {
    }

    public CreateChatRequestDTO(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}