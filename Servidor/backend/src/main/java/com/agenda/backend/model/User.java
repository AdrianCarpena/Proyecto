package com.agenda.backend.model;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}