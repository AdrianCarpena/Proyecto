package com.agenda.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.backend.model.Examen;
import com.agenda.backend.model.User;

public interface ExamenRepository extends JpaRepository<Examen, Long> {

    List<Examen> findByUser(User user);

    List<Examen> findByUserAndFechaAfter(User user, LocalDate fecha);
}