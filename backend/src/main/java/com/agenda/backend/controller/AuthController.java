package com.agenda.backend.controller;

import com.agenda.backend.service.AuthService;
import com.agenda.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody User request) {
        return authService.register(request.getUsername(), request.getPassword());
    }

    @PostMapping("/login")
    public String login(@RequestBody User request) {
        return authService.login(request.getUsername(), request.getPassword());
    }
}