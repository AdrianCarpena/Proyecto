package com.agenda.backend.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

//Indicamos que es una entidad JPA
@Entity
//Nombre que tendrá la tabla
@Table(name = "users")
//Generar constructor vacio automáticamente sin código
@NoArgsConstructor
//Generar constructor con todos los parámetros sin código
@AllArgsConstructor
//Anotación para poder instanciar entidades con .build (más comodo)
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Un usuario puede tener muchas tareas y exámenes
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Tarea> tareas;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Examen> examenes;

    // Un usuario puede tener muchas sesiones de estudio
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<StudySession> studySessions;


}