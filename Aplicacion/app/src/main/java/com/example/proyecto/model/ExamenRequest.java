package com.example.proyecto.model;

public class ExamenRequest {

    private String asignatura;
    private String fecha;
    private String prioridad;
    private String dificultad;


    public ExamenRequest() {}

    public ExamenRequest(String asignatura, String fecha, String prioridad, String dificultad) {
        this.asignatura = asignatura;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.dificultad = dificultad;
    }

    public String getAsignatura() { return asignatura; }
    public String getFecha() { return fecha; }
    public String getPrioridad() { return prioridad; }
    public String getDificultad() { return dificultad; }
}