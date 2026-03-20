package com.agenda.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "study_sessions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
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

}