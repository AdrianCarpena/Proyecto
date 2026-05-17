package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.ExamenResponseDTO;
import com.agenda.backend.model.Examen;
import com.agenda.backend.model.User;
import com.agenda.backend.service.ExamenService;
import com.agenda.backend.repository.UserRepository;

@RestController
@RequestMapping("/exams")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    //Metodo para obtener el DTO de la entidad
    private ExamenResponseDTO toDTO(Examen examen) {
        return new ExamenResponseDTO(
                examen.getId(),
                examen.getAsignatura(),
                examen.getDificultad().name(),
                examen.getPrioridad().name(),
                examen.getHorasEstimadas(),
                examen.getFecha(),
                examen.getUser().getId()
        );
    }

    @PostMapping
    public ResponseEntity<ExamenResponseDTO> createExam(@RequestBody Examen examen, Principal principal) {
        User user = getUser(principal);
        Examen saved = examenService.createExam(examen, user);
        return ResponseEntity.status(201).body(toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<ExamenResponseDTO>> getExams(Principal principal) {
        User user = getUser(principal);

        List<ExamenResponseDTO> response = examenService.getExams(user)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        examenService.deleteExam(id, user);
        return ResponseEntity.ok("Examen eliminado");
    }
}