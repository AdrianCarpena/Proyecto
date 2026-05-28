package com.example.proyecto.model;

public class TareaResponse {

    private Long id;
    private String asignatura;
    private String dificultad;
    private String prioridad;
    private Integer horasEstimadas;
    private String fecha; // 🔥 IMPORTANTE: String, no LocalDate
    private Long userId;

    public Long getId() { return id; }
    public String getAsignatura() { return asignatura; }
    public String getDificultad() { return dificultad; }
    public String getPrioridad() { return prioridad; }
    public Integer getHorasEstimadas() { return horasEstimadas; }
    public String getFecha() { return fecha; }
    public Long getUserId() { return userId; }
}