package com.agenda.backend.service;

import static org.junit.jupiter.api.Assertions.*;
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
import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.StudySessionRepository;

@ExtendWith(MockitoExtension.class)
public class StudySessionServiceTest {

    @Mock
    private StudySessionRepository studySessionRepository;

    @InjectMocks
    private StudySessionService studySessionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Rober");
    }

    @Test
    void noDebeMoverSesionDespuesDeFechaLimite() {
        Examen examen = new Examen();
        examen.setId(1L);
        examen.setFecha(LocalDate.now().plusDays(5));

        StudySession session = new StudySession();
        session.setId(1L);
        session.setUser(user);
        session.setExamen(examen);
        session.setFecha(LocalDate.now().plusDays(1));
        session.setCheck(false);

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(IllegalArgumentException.class, () -> {
            studySessionService.moverSesion(1L, user, LocalDate.now().plusDays(6));
        });

        verify(studySessionRepository, never()).save(any(StudySession.class));
    }

    @Test
    void marcarSesionComoHechaDebePonerCheckTrue() {
        StudySession session = new StudySession();
        session.setId(1L);
        session.setUser(user);
        session.setCheck(false);

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(studySessionRepository.save(any(StudySession.class))).thenReturn(session);

        StudySession result = studySessionService.marcarComoHecha(1L, user);

        assertTrue(result.isCheck());
        verify(studySessionRepository).save(session);
    }
}