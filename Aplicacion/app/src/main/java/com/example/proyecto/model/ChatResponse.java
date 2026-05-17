package com.example.proyecto.model;

public class ChatResponse {
    private Long id;
    private String nombre;
    private String joinCode;
    private Long creatorId;
    private String creatorUsername;
    private int memberCount;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getJoinCode() { return joinCode; }
    public Long getCreatorId() { return creatorId; }
    public String getCreatorUsername() { return creatorUsername; }
    public int getMemberCount() { return memberCount; }
}