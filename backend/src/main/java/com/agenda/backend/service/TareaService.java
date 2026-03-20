package com.agenda.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.TareaRepository;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private PlanningService planningService; // 🔥 importante

    public Tarea createTask(Tarea tarea, User user) {
        tarea.setUser(user);
        Tarea saved = tareaRepository.save(tarea);

        // 🔥 recalcular plan
        planningService.recalculatePlan(user);

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

        tareaRepository.delete(tarea);

        // 🔥 recalcular plan
        planningService.recalculatePlan(user);
    }
}