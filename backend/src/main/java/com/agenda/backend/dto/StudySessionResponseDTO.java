package com.agenda.backend.dto;

import java.time.LocalDate;

public class StudySessionResponseDTO {

    private Long id;
    private LocalDate fecha;
    private int duracionHoras;
    private boolean check;
    private Long userId;
    private Long examenId;
    private Long tareaId;
    private String asignatura;

    public StudySessionResponseDTO() {
    }

    public StudySessionResponseDTO(Long id, LocalDate fecha, int duracionHoras, boolean check,
                                   Long userId, Long examenId, Long tareaId, String asignatura) {
        this.id = id;
        this.fecha = fecha;
        this.duracionHoras = duracionHoras;
        this.check = check;
        this.userId = userId;
        this.examenId = examenId;
        this.tareaId = tareaId;
        this.asignatura = asignatura;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public boolean isCheck() {
        return check;
    }

    public Long getUserId() {
        return userId;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setDuracionHoras(int duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
    }

    public void setTareaId(Long tareaId) {
        this.tareaId = tareaId;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }
}