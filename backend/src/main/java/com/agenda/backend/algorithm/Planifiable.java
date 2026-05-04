package com.agenda.backend.algorithm;

import java.time.LocalDate;

import com.agenda.backend.model.User;
import com.agenda.backend.model.enums.Dificultad;
import com.agenda.backend.model.enums.Prioridad;

public interface Planifiable {
    User getUser();
    LocalDate getFecha();      // fecha límite
    Integer getHorasEstimadas();   // total de horas calculadas
    String getAsignatura();        // para la descripción de la sesión
    Dificultad getDificultad();
    Prioridad getPrioridad();
}