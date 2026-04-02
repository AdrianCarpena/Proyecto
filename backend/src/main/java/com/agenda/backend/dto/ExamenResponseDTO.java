package com.agenda.backend.dto;

import java.time.LocalDate;

public class ExamenResponseDTO {

    private Long id;
    private String asignatura;
    private String dificultad;
    private String prioridad;
    private Integer horasEstimadas;
    private LocalDate fecha;
    private Long userId;

    public ExamenResponseDTO() {
    }

    public ExamenResponseDTO(Long id, String asignatura, String dificultad, String prioridad,Integer horasEstimadas, LocalDate fecha, Long userId) {
        this.id = id;
        this.asignatura = asignatura;
        this.dificultad = dificultad;
        this.prioridad = prioridad;
        this.horasEstimadas = horasEstimadas;
        this.fecha = fecha;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(Integer horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}