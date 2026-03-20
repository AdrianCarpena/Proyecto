package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<?> createBusy(@RequestBody BusyHours busyHours, Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(busyHoursService.createBusyHours(busyHours, user));
    }

    @GetMapping
    public ResponseEntity<List<BusyHours>> getBusy(Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(busyHoursService.getBusyHours(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBusy(@PathVariable Long id, Principal principal) {
        User user = getUser(principal);
        busyHoursService.deleteBusyHours(id, user);
        return ResponseEntity.ok("Busy hours eliminadas");
    }
}