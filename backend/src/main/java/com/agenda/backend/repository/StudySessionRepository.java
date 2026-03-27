package com.agenda.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.backend.model.Examen;
import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByUser(User user);

    List<StudySession> findByUserAndFecha(User user, LocalDate fecha);

    void deleteByUser(User user); // clave para recalcular el plan

    List<StudySession> findByUserAndFechaBetween(User user, LocalDate start, LocalDate end);

    void deleteByExamen(Examen examen);

    void deleteByTarea(Tarea tarea);
}