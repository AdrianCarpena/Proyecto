package com.agenda.backend.dto;

import java.time.LocalDateTime;

public class ChatResponseDTO {

    private Long id;
    private String nombre;
    private String joinCode;
    private Long creatorId;
    private String creatorUsername;
    private int memberCount;
    private LocalDateTime createdAt;

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(Long id, String nombre, String joinCode, Long creatorId, String creatorUsername, int memberCount, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.joinCode = joinCode;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.memberCount = memberCount;
        this.createdAt = createdAt;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}