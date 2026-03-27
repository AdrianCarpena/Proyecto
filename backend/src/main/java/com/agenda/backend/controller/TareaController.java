package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;
import com.agenda.backend.service.TareaService;
import com.agenda.backend.repository.UserRepository;

@RestController
@RequestMapping("/tasks")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody Tarea tarea, Principal principal) {
        try{
            User user = getUser(principal);
            return ResponseEntity.ok(tareaService.createTask(tarea, user));
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> getTasks(Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(tareaService.getTasks(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        tareaService.deleteTask(id, user);
        return ResponseEntity.ok("Tarea eliminada");
    }
}