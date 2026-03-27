package com.agenda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.StudySessionRepository;

import java.time.LocalDate;

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

    public StudySession moverSesion(Long id, User user, LocalDate nuevaFecha) {

        StudySession session = studySessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        // No permitir mover a fecha pasada
        if (nuevaFecha.isBefore(LocalDate.now())) {
            throw new RuntimeException("No puedes mover una sesión a una fecha pasada");
        }

        // No mover más allá de la fecha límite del examen o tarea
        if (session.getExamen() != null && !nuevaFecha.isBefore(session.getExamen().getFecha())) {
            throw new RuntimeException("No puedes mover la sesión a después del examen");
        }

        if (session.getTarea() != null && nuevaFecha.isAfter(session.getTarea().getFecha())) {
            throw new RuntimeException("No puedes mover la sesión a después de la fecha límite de la tarea");
        }

        session.setFecha(nuevaFecha);
        return studySessionRepository.save(session);
    }
}