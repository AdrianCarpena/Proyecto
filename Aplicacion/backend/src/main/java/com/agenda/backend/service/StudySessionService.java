package com.agenda.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.exception.ForbiddenException;
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
        	throw new ForbiddenException("No autorizado");
        }

        session.setCheck(true);
        return studySessionRepository.save(session);
    }
    public List<StudySession> findByUser(User user) {
        return studySessionRepository.findByUser(user);
    }

    // 🔁 Mover sesión a otro día
    public StudySession moverSesion(Long id, User user, java.time.LocalDate nuevaFecha) {

        StudySession session = studySessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!session.getUser().getId().equals(user.getId())) {
        	throw new ForbiddenException("No autorizado");
        }
        
        //Comprobacion de que la fecha nueva está entre la fecha actual y la del dia anterior al examen o tarea asociada
        LocalDate hoy = LocalDate.now();

        if (nuevaFecha.isBefore(hoy)) {
            throw new IllegalArgumentException("No puedes mover una sesión a una fecha pasada");
        }

        if (session.getExamen() != null) {
            LocalDate fechaLimite = session.getExamen().getFecha().minusDays(1);
            if (nuevaFecha.isAfter(fechaLimite)) {
                throw new IllegalArgumentException("La sesión no puede moverse después del día anterior al examen");
            }
        }

        if (session.getTarea() != null) {
            LocalDate fechaLimite = session.getTarea().getFecha().minusDays(1);
            if (nuevaFecha.isAfter(fechaLimite)) {
                throw new IllegalArgumentException("La sesión no puede moverse después del día anterior a la fecha límite de la tarea");
            }
        }

        session.setFecha(nuevaFecha);
        return studySessionRepository.save(session);
    }
}