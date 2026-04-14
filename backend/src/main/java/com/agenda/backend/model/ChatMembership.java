package com.agenda.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_memberships",uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "user_id"}))
public class ChatMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    public ChatMembership() {
    }

    public ChatMembership(Long id, Chat chat, User user, LocalDateTime joinedAt) {
        this.id = id;
        this.chat = chat;
        this.user = user;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public Chat getChat() {
        return chat;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}