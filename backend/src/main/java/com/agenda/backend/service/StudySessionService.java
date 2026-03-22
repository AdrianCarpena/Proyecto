package com.agenda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.StudySessionRepository;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository studySessionRepository;

    // ✅ Marcar como hecha
    public StudySession marcarComoHecha(Long id, User user) {

        StudySession session = studySessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        session.setCheck(true);
        return studySessionRepository.save(session);
    }

    // 🔁 Mover sesión a otro día
    public StudySession moverSesion(Long id, User user, java.time.LocalDate nuevaFecha) {

        StudySession session = studySessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        session.setFecha(nuevaFecha);
        return studySessionRepository.save(session);
    }
}