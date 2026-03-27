package com.agenda.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.model.BusyHours;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.BusyHoursRepository;

@Service
public class BusyHoursService {

    @Autowired
    private BusyHoursRepository busyHoursRepository;


    public BusyHours createBusyHours(BusyHours busyHours, User user) {
        busyHours.setUser(user);
        BusyHours saved = busyHoursRepository.save(busyHours);


        return saved;
    }

    public List<BusyHours> getBusyHours(User user) {
        return busyHoursRepository.findByUser(user);
    }

    public void deleteBusyHours(Long id, User user) {
        BusyHours busy = busyHoursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BusyHours no encontrado"));

        if (!busy.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        busyHoursRepository.delete(busy);
    }
}