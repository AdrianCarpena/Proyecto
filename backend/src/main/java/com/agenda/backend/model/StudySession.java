package com.agenda.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "study_sessions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySession{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha; // día de la sesión

    private int duracionHoras; // duración en horas

    private boolean check; // si se ha completado

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Examen examen; // puede ser null si es sesión independiente

    @ManyToOne
    @JoinColumn(name = "tarea_id")
    private Tarea tarea; // puede ser null si es sesión independiente

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(int duracionHoras) { this.duracionHoras = duracionHoras; }

    public boolean isCheck() { return check; }
    public void setCheck(boolean check) { this.check = check; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Examen getExamen() { return examen; }
    public void setExamen(Examen examen) { this.examen = examen; }

    public Tarea getTarea() { return tarea; }
    public void setTarea(Tarea tarea) { this.tarea = tarea; }
}