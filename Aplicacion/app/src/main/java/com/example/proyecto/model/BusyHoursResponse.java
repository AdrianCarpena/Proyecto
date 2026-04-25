package com.example.proyecto.model;

public class BusyHoursResponse {

    private Long id;
    private String titulo;
    private String fecha;
    private int duracionHoras;
    private Long userId;

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public int getDuracionHoras() { return duracionHoras; }
    public Long getUserId() { return userId; }
}