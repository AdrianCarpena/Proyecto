package com.agenda.backend.dto;

import java.time.LocalDate;

public class CalendarEventDTO {

    private Long id;

    private String tipo;
    // "STUDY", "EXAMEN", "TAREA", "BUSY"

    private String asignatura;

    private String titulo;
    // para BusyHours (ej: "Clase", "Trabajo")

    private LocalDate fecha;

    private int duracionHoras;

    private boolean completado;

    public CalendarEventDTO() {}

    public CalendarEventDTO(Long id, String tipo, String asignatura, String titulo,LocalDate fecha, int duracionHoras, boolean completado) {
        this.id = id;
        this.tipo = tipo;
        this.asignatura = asignatura;
        this.titulo = titulo;
        this.fecha = fecha;
        this.duracionHoras = duracionHoras;
        this.completado = completado;
    }

    // GETTERS Y SETTERS

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAsignatura() { return asignatura; }
    public void setAsignatura(String asignatura) { this.asignatura = asignatura; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }

    public boolean isCompletado() { return completado; }
    public void setCompletado(boolean completado) { this.completado = completado; }
}