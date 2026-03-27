package com.agenda.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agenda.backend.model.BusyHours;
import com.agenda.backend.model.User;

public interface BusyHoursRepository extends JpaRepository<BusyHours, Long> {

    List<BusyHours> findByUser(User user);

    List<BusyHours> findByUserAndFecha(User user, LocalDate fecha);

}