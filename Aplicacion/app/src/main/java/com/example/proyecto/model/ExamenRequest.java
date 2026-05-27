package com.example.proyecto.model;

public class ExamenRequest {

    private String asignatura;
    private String fecha;
    private String prioridad;
    private String dificultad;
    private Integer horasEstimadas;

    public ExamenRequest() {}

    public ExamenRequest(String asignatura,
                         String fecha,
                         String prioridad,
                         String dificultad,
                         Integer horasEstimadas) {

        this.asignatura = asignatura;
        this.fecha = fecha;
        this.prioridad = prioridad;
        this.dificultad = dificultad;
        this.horasEstimadas = horasEstimadas;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public String getFecha() {
        return fecha;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getDificultad() {
        return dificultad;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public void setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }
}