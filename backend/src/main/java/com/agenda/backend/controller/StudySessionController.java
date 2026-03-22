package com.agenda.backend.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.service.StudySessionService;

@RestController
@RequestMapping("/sessions")
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ✅ Marcar como completada
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completarSesion(@PathVariable Long id, Principal principal) {

        User user = getUser(principal);
        return ResponseEntity.ok(studySessionService.marcarComoHecha(id, user));
    }

    // 🔁 Mover sesión
    @PatchMapping("/{id}/move")
    public ResponseEntity<?> moverSesion(@PathVariable Long id,
                                         @RequestParam String fecha,
                                         Principal principal) {

        User user = getUser(principal);
        LocalDate nuevaFecha = LocalDate.parse(fecha);

        return ResponseEntity.ok(
                studySessionService.moverSesion(id, user, nuevaFecha)
        );
    }
}