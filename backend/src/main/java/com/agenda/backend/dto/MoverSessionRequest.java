package com.agenda.backend.dto;

import java.time.LocalDate;

public class MoverSessionRequest {

    private LocalDate fecha;

    public MoverSessionRequest() {}

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}