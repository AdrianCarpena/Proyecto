package com.agenda.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    
    
    //Atributos para el chat, JsonIgnore es para que no mande este atributo al frontend al generar el json
    @JsonIgnore
    @OneToMany(mappedBy = "creator")
    private List<Chat> createdChats;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ChatMembership> chatMemberships;

    @JsonIgnore
    @OneToMany(mappedBy = "sender")
    private List<Message> messages;
    
	
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Tarea> getTareas() { return tareas; }
    public void setTareas(List<Tarea> tareas) { this.tareas = tareas; }

    public List<Examen> getExamenes() { return examenes; }
    public void setExamenes(List<Examen> examenes) { this.examenes = examenes; }

    public List<StudySession> getStudySessions() { return studySessions; }
    public void setStudySessions(List<StudySession> studySessions) { this.studySessions = studySessions; }
}
