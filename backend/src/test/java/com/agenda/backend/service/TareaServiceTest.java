package com.agenda.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;
import com.agenda.backend.model.enums.Dificultad;
import com.agenda.backend.model.enums.Prioridad;
import com.agenda.backend.repository.StudySessionRepository;
import com.agenda.backend.repository.TareaRepository;

@ExtendWith(MockitoExtension.class)
public class TareaServiceTest {

    @Mock
    private TareaRepository tareaRepository;

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private PlanningService planningService;

    @InjectMocks
    private TareaService tareaService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Rober");
    }

    @Test
    void noDebeCrearTareaConFechaPasada() {
        Tarea tarea = new Tarea();
        tarea.setAsignatura("Programacion");
        tarea.setFecha(LocalDate.now().minusDays(1));
        tarea.setDificultad(Dificultad.MEDIA);
        tarea.setPrioridad(Prioridad.MEDIA);

        assertThrows(IllegalArgumentException.class, () -> {
            tareaService.createTask(tarea, user);
        });

        verify(tareaRepository, never()).save(any(Tarea.class));
    }

    @Test
    void noDebeCrearTareaConHorasEstimadasNegativas() {
        Tarea tarea = new Tarea();
        tarea.setAsignatura("Programacion");
        tarea.setFecha(LocalDate.now().plusDays(5));
        tarea.setDificultad(Dificultad.MEDIA);
        tarea.setPrioridad(Prioridad.MEDIA);
        tarea.setHorasEstimadas(-3);

        assertThrows(IllegalArgumentException.class, () -> {
            tareaService.createTask(tarea, user);
        });

        verify(tareaRepository, never()).save(any(Tarea.class));
    }
}