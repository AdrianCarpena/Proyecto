package com.agenda.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByUser(User user);
}