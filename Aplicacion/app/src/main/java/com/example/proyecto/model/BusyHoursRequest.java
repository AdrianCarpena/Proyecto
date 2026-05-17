package com.example.proyecto.model;

public class BusyHoursRequest {

    private String titulo;
    private String fecha;
    private int duracionHoras;

    public BusyHoursRequest(String titulo, String fecha, int duracionHoras) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.duracionHoras = duracionHoras;
    }
}