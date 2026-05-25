package com.example.proyecto.model;

public class ChangeUsernameRequest {

    private String newUsername;

    public ChangeUsernameRequest(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}