package com.example.proyecto.model;

public class StudySessionResponse {

    private Long id;
    private String fecha;
    private int duracionHoras;
    private boolean check;
    private Long examenId;
    private Long tareaId;
    private String asignatura;

    public Long getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public boolean isCheck() {
        return check;
    }

    public Long getExamenId() {
        return examenId;
    }

    public Long getTareaId() {
        return tareaId;
    }

    public String getAsignatura() {
        return asignatura;
    }
}