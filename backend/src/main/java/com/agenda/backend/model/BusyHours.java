package com.agenda.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "busy_hours")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusyHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;

    private LocalDate fecha; // día ocupado

    private int duracionHoras; // horas ocupadas en ese día

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
	public String getTitulo() {return titulo;	}
	public void setTitulo(String titulo) {this.titulo = titulo;	}
}