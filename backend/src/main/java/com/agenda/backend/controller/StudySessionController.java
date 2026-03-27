package com.agenda.backend.controller;

import java.security.Principal;
import java.time.LocalDate;

import com.agenda.backend.dto.MoverSessionRequest;
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

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completarSesion(@PathVariable Long id, Principal principal) {
        try {
            User user = getUser(principal);
            return ResponseEntity.ok(studySessionService.marcarComoHecha(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<?> moverSesion(@PathVariable Long id,
                                         @RequestBody MoverSessionRequest request,
                                         Principal principal) {
        try {
            User user = getUser(principal);
            return ResponseEntity.ok(
                    studySessionService.moverSesion(id, user, request.getFecha())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}