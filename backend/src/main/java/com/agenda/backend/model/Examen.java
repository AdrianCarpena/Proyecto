package com.agenda.backend.model;

import com.agenda.backend.algorithm.Planifiable;
import com.agenda.backend.model.enums.Dificultad;
import com.agenda.backend.model.enums.Prioridad;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "examenes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Examen implements Planifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asignatura;

    private int horasEstimadas; // horas necesarias de estudio

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;

    private LocalDate fecha; // fecha del examen

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}