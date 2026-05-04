package com.agenda.backend.dto;

public class UserProfileWithTokenDTO {

    private Long id;
    private String username;
    private String token;

    public UserProfileWithTokenDTO(Long id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }
}