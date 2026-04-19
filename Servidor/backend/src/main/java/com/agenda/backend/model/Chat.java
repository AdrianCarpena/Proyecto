package com.agenda.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String joinCode;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMembership> memberships;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    public Chat() {
    }

    public Chat(Long id, String nombre, String joinCode, User creator, LocalDateTime createdAt,
                List<ChatMembership> memberships, List<Message> messages) {
        this.id = id;
        this.nombre = nombre;
        this.joinCode = joinCode;
        this.creator = creator;
        this.createdAt = createdAt;
        this.memberships = memberships;
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public User getCreator() {
        return creator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ChatMembership> getMemberships() {
        return memberships;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMemberships(List<ChatMembership> memberships) {
        this.memberships = memberships;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}