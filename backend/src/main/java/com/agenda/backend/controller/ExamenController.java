package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<?> createExam(@RequestBody Examen examen, Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(examenService.createExam(examen, user));
    }

    @GetMapping
    public ResponseEntity<List<Examen>> getExams(Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(examenService.getExams(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        examenService.deleteExam(id, user);
        return ResponseEntity.ok("Examen eliminado");
    }
}