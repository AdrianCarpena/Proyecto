package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.BusyHoursResponseDTO;
import com.agenda.backend.model.BusyHours;
import com.agenda.backend.model.User;
import com.agenda.backend.service.BusyHoursService;
import com.agenda.backend.repository.UserRepository;

@RestController
@RequestMapping("/busy-slots")
public class BusyHoursController {

    @Autowired
    private BusyHoursService busyHoursService;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    //Metodo para pasar a DTO la entidad
    private BusyHoursResponseDTO toDTO(BusyHours busyHours) {
        return new BusyHoursResponseDTO(
                busyHours.getId(),
                busyHours.getTitulo(),
                busyHours.getFecha(),
                busyHours.getDuracionHoras(),
                busyHours.getUser().getId()
        );
    }

    @PostMapping
    public ResponseEntity<BusyHoursResponseDTO> createBusy(@RequestBody BusyHours busyHours, Principal principal) {
        User user = getUser(principal);
        BusyHours saved = busyHoursService.createBusyHours(busyHours, user);
        return ResponseEntity.status(201).body(toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<BusyHoursResponseDTO>> getBusy(Principal principal) {
        User user = getUser(principal);

        List<BusyHoursResponseDTO> response = busyHoursService.getBusyHours(user)
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBusy(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        busyHoursService.deleteBusyHours(id, user);
        return ResponseEntity.ok("Busy hours eliminadas");
    }
}