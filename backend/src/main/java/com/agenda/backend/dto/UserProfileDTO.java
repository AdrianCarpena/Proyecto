package com.agenda.backend.dto;

public class UserProfileDTO {
    private Long id;
    private String username;

    public UserProfileDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
}