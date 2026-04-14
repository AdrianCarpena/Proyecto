package com.agenda.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    public Message() {
    }

    public Message(Long id, String content, Chat chat, User sender, LocalDateTime sentAt) {
        this.id = id;
        this.content = content;
        this.chat = chat;
        this.sender = sender;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Chat getChat() {
        return chat;
    }

    public User getSender() {
        return sender;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}