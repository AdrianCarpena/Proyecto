package com.agenda.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.model.Examen;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.ExamenRepository;

@Service
public class ExamenService {

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private PlanningService planningService;

    public Examen createExam(Examen examen, User user) {
        examen.setUser(user);
        Examen saved = examenRepository.save(examen);

        // 🔥 recalcular plan
        planningService.recalculatePlan(user);

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

        examenRepository.delete(examen);

        // 🔥 recalcular plan
        planningService.recalculatePlan(user);
    }
}