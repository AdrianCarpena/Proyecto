package com.example.proyecto.model;

public class TareaRequest {

    private String asignatura;
    private String fecha;        // "2026-04-17"
    private String prioridad;    // "ALTA"
    private String dificultad;   // "MEDIA"

    public TareaRequest(String asignatura, String fecha,
                        String prioridad, String dificultad) {
        this.asignatura = asignatura;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.dificultad = dificultad;
    }
}