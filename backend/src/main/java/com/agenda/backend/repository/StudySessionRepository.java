package com.agenda.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.User;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    List<StudySession> findByUser(User user);

    List<StudySession> findByUserAndFecha(User user, LocalDate fecha);

    void deleteByUser(User user); // clave para recalcular el plan
}