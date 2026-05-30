package com.agenda.backend.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.StudySessionResponseDTO;
import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.service.PlanningService;
import com.agenda.backend.service.StudySessionService;

@RestController
@RequestMapping("/sessions")
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PlanningService planningService;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    //Metodo para convertir la entidad en un DTO
    private StudySessionResponseDTO toDTO(StudySession session) {
        Long examenId = session.getExamen() != null ? session.getExamen().getId() : null;
        Long tareaId = session.getTarea() != null ? session.getTarea().getId() : null;

        String asignatura = null;
        if (session.getExamen() != null) {
            asignatura = session.getExamen().getAsignatura();
        } else if (session.getTarea() != null) {
            asignatura = session.getTarea().getAsignatura();
        }

        return new StudySessionResponseDTO(
                session.getId(),
                session.getFecha(),
                session.getDuracionHoras(),
                session.isCheck(),
                session.getUser().getId(),
                examenId,
                tareaId,
                asignatura
        );
    }

    @GetMapping
    public ResponseEntity<List<StudySessionResponseDTO>> getSessions(Principal principal) {

        User user = getUser(principal);

        List<StudySession> sessions = studySessionService.findByUser(user);

        return ResponseEntity.ok(
                sessions.stream()
                        .map(this::toDTO)
                        .toList()
        );
    }

    // ✅ Marcar como completada
    @PatchMapping("/{id}/complete")
    public ResponseEntity<StudySessionResponseDTO> completarSesion(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(toDTO(studySessionService.marcarComoHecha(id, user)));
    }

    // 🔁 Mover sesión
    @PatchMapping("/{id}/move")
    public ResponseEntity<StudySessionResponseDTO> moverSesion(@PathVariable Long id,
                                                               @RequestParam String fecha,
                                                               Principal principal) {
        User user = getUser(principal);
        LocalDate nuevaFecha = LocalDate.parse(fecha);

        return ResponseEntity.ok(
                toDTO(studySessionService.moverSesion(id, user, nuevaFecha))
        );
    }
    

    @PostMapping("/reubicar")
    public ResponseEntity<?> reubicarSesionesNoHechas(Principal principal) {
        User user = getUser(principal);
        planningService.reubicarSesionesNoHechas(user);
        return ResponseEntity.ok("Sesiones no hechas reubicadas");
    }
}