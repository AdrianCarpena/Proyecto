package com.agenda.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            throw new RuntimeException("No autorizado");
        }
        
        studySessionRepository.deleteByExamen(examen);

        examenRepository.delete(examen);
    }
}