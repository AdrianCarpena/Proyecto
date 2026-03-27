package com.agenda.backend.model;

import com.agenda.backend.algorithm.Planifiable;
import com.agenda.backend.model.enums.Dificultad;
import com.agenda.backend.model.enums.Prioridad;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tareas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Tarea implements Planifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asignatura;

    @Min(value = 1, message = "Las horas estimadas deben ser un número entero positivo")
    private int horasEstimadas; // horas necesarias para completar la tarea

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;

    private LocalDate fecha; // fecha límite para la tarea

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}