package com.example.proyecto.model;

public class CalendarEventResponse {

    private Long id;
    private String tipo;
    private String asignatura;
    private String titulo;
    private String fecha;
    private int duracionHoras;
    private boolean completado;

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getAsignatura() { return asignatura; }
    public String getTitulo() { return titulo; }
    public String getFecha() { return fecha; }
    public int getDuracionHoras() { return duracionHoras; }
    public boolean isCompletado() { return completado; }
}