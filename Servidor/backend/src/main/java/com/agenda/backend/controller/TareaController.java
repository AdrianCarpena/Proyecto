package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.TareaResponseDTO;
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
    
    //Metodo para pasar a DTO la entidad
    private TareaResponseDTO toDTO(Tarea tarea) {
        return new TareaResponseDTO(
                tarea.getId(),
                tarea.getAsignatura(),
                tarea.getDificultad().name(),
                tarea.getPrioridad().name(),
                tarea.getHorasEstimadas(),
                tarea.getFecha(),
                tarea.getUser().getId()
        );
    }

    @PostMapping
    public ResponseEntity<TareaResponseDTO> createTask(@RequestBody Tarea tarea, Principal principal) {
        User user = getUser(principal);
        Tarea saved = tareaService.createTask(tarea, user);
        return ResponseEntity.status(201).body(toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<TareaResponseDTO>> getTasks(Principal principal) {
        User user = getUser(principal);

        List<TareaResponseDTO> response = tareaService.getTasks(user)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        tareaService.deleteTask(id, user);
        return ResponseEntity.ok("Tarea eliminada");
    }
}