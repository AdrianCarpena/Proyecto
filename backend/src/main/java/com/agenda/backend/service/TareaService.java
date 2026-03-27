package com.agenda.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.StudySessionRepository;
import com.agenda.backend.repository.TareaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private PlanningService planningService; // 🔥 importante

    public Tarea createTask(Tarea tarea, User user) {
        if (tarea.getFecha().isBefore(LocalDate.now().plusDays(2))) {
            throw new RuntimeException("La fecha de la tarea debe ser al menos 2 días después de hoy");
        }
        tarea.setUser(user);
        Tarea saved = tareaRepository.save(tarea);
        planningService.generarPlanParaEvento(user, tarea);
        return saved;
    }

    public List<Tarea> getTasks(User user) {
        return tareaRepository.findByUser(user);
    }

    public void deleteTask(Long id, User user) {
        Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!tarea.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        studySessionRepository.deleteByTarea(tarea);

        tareaRepository.delete(tarea);

    }
}