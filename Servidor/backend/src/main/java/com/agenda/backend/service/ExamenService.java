package com.agenda.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.exception.ForbiddenException;
import com.agenda.backend.model.Examen;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.ExamenRepository;
import com.agenda.backend.repository.StudySessionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ExamenService {

    @Autowired
    private ExamenRepository examenRepository;
    
    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private PlanningService planningService;

    public Examen createExam(Examen examen, User user) {
        examen.setUser(user);
        
        if (!examen.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del examen debe ser posterior a hoy");
        }
        
        if (examen.getHorasEstimadas() != null && examen.getHorasEstimadas() <= 0) {
            throw new IllegalArgumentException("Las horas estimadas deben ser un número entero positivo");
        }
        
        Examen saved = examenRepository.save(examen);

        // 🔥 recalcular plan
        planningService.generarPlanParaEvento(user,examen);

        return saved;
    }

    public List<Examen> getExams(User user) {
        return examenRepository.findByUser(user);
    }

    public void deleteExam(Long id, User user) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen no encontrado"));

        if (!examen.getUser().getId().equals(user.getId())) {
        	throw new ForbiddenException("No autorizado");
        }
        
        studySessionRepository.deleteByExamen(examen);

        examenRepository.delete(examen);
    }
}