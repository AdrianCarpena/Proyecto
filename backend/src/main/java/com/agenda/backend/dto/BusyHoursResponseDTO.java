package com.agenda.backend.dto;

import java.time.LocalDate;

public class BusyHoursResponseDTO {

    private Long id;
    private String titulo;
    private LocalDate fecha;
    private int duracionHoras;
    private Long userId;

    public BusyHoursResponseDTO() {
    }

    public BusyHoursResponseDTO(Long id, String titulo, LocalDate fecha, int duracionHoras, Long userId) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.duracionHoras = duracionHoras;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setDuracionHoras(int duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}