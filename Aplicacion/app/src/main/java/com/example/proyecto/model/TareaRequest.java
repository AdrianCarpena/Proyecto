package com.example.proyecto.model;

public class TareaRequest {

    private String asignatura;
    private String fecha;
    private String prioridad;
    private String dificultad;
    private Integer horasEstimadas;

    public TareaRequest() {}

    public TareaRequest(String asignatura,
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
}