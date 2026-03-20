package com.agenda.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "busy_hours")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BusyHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha; // día ocupado

    private int duracionHoras; // horas ocupadas en ese día

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}