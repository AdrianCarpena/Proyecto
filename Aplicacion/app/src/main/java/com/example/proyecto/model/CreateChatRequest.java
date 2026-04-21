package com.example.proyecto.model;

public class CreateChatRequest {
    private String nombre;
    public CreateChatRequest(String nombre) { this.nombre = nombre; }
    public String getNombre() { return nombre; }
}