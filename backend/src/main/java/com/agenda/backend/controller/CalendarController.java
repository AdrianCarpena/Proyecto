package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.CalendarEventDTO;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.service.CalendarService;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<CalendarEventDTO> getCalendario(Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow();

        return calendarService.getCalendarioCompleto(user);
    }
    
    @GetMapping("/today")
    public List<CalendarEventDTO> getEventosDeHoy(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return calendarService.getEventosDeHoy(user);
    }
}