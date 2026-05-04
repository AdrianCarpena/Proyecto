package com.agenda.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.dto.CalendarEventDTO;
import com.agenda.backend.model.BusyHours;
import com.agenda.backend.model.Examen;
import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.BusyHoursRepository;
import com.agenda.backend.repository.ExamenRepository;
import com.agenda.backend.repository.StudySessionRepository;
import com.agenda.backend.repository.TareaRepository;

@Service
public class CalendarService {

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private BusyHoursRepository busyHoursRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private TareaRepository tareaRepository;

    public List<CalendarEventDTO> getCalendarioCompleto(User user) {

        List<CalendarEventDTO> eventos = new ArrayList<>();

        // 🔵 Study Sessions
        List<StudySession> sesiones = studySessionRepository.findByUser(user);

        for (StudySession s : sesiones) {
            CalendarEventDTO dto = new CalendarEventDTO();

            dto.setId(s.getId());
            dto.setTipo("STUDY");
            dto.setFecha(s.getFecha());
            dto.setDuracionHoras(s.getDuracionHoras());
            dto.setCompletado(s.isCheck());

            if (s.getExamen() != null) {
                dto.setAsignatura(s.getExamen().getAsignatura());
            } else if (s.getTarea() != null) {
                dto.setAsignatura(s.getTarea().getAsignatura());
            }

            eventos.add(dto);
        }

        // 🔴 Busy Hours
        List<BusyHours> busyList = busyHoursRepository.findByUser(user);

        for (BusyHours b : busyList) {
            CalendarEventDTO dto = new CalendarEventDTO();

            dto.setId(b.getId());
            dto.setTipo("BUSY");
            dto.setFecha(b.getFecha());
            dto.setDuracionHoras(b.getDuracionHoras());
            dto.setTitulo(b.getTitulo());

            eventos.add(dto);
        }

        // 🟡 Exámenes
        List<Examen> examenes = examenRepository.findByUser(user);

        for (Examen e : examenes) {
            CalendarEventDTO dto = new CalendarEventDTO();

            dto.setId(e.getId());
            dto.setTipo("EXAMEN");
            dto.setFecha(e.getFecha());
            dto.setAsignatura(e.getAsignatura());
            dto.setDuracionHoras(0);
            dto.setCompletado(false);

            eventos.add(dto);
        }

        // 🟢 Tareas
        List<Tarea> tareas = tareaRepository.findByUser(user);

        for (Tarea t : tareas) {
            CalendarEventDTO dto = new CalendarEventDTO();

            dto.setId(t.getId());
            dto.setTipo("TAREA");
            dto.setFecha(t.getFecha());
            dto.setAsignatura(t.getAsignatura());
            dto.setDuracionHoras(0);
            dto.setCompletado(false);

            eventos.add(dto);
        }

        return eventos;
    }

    public List<CalendarEventDTO> getEventosDeHoy(User user) {
        LocalDate hoy = LocalDate.now();

        return getCalendarioCompleto(user)
                .stream()
                .filter(evento -> hoy.equals(evento.getFecha()))
                .toList();
    }
}