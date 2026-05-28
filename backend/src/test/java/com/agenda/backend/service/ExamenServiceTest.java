package com.agenda.backend.service;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agenda.backend.model.Examen;
import com.agenda.backend.model.User;
import com.agenda.backend.model.enums.Dificultad;
import com.agenda.backend.model.enums.Prioridad;
import com.agenda.backend.repository.ExamenRepository;
import com.agenda.backend.repository.StudySessionRepository;

@ExtendWith(MockitoExtension.class)
public class ExamenServiceTest {

    @Mock
    private ExamenRepository examenRepository;

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private PlanningService planningService;

    @InjectMocks
    private ExamenService examenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Rober");
    }

    @Test
    void crearExamenValidoDebeGenerarPlan() {
        Examen examen = new Examen();
        examen.setId(1L);
        examen.setAsignatura("Bases de Datos");
        examen.setFecha(LocalDate.now().plusDays(10));
        examen.setDificultad(Dificultad.MEDIA);
        examen.setPrioridad(Prioridad.ALTA);

        when(examenRepository.save(any(Examen.class))).thenReturn(examen);

        examenService.createExam(examen, user);

        verify(examenRepository).save(examen);
        verify(planningService).generarPlanParaEvento(user, examen);
    }

    @Test
    void eliminarExamenDebeBorrarSusSesionesAsociadas() {
        Examen examen = new Examen();
        examen.setId(1L);
        examen.setUser(user);

        when(examenRepository.findById(1L)).thenReturn(Optional.of(examen));

        examenService.deleteExam(1L, user);

        verify(studySessionRepository).deleteByExamen(examen);
        verify(examenRepository).delete(examen);
    }
}