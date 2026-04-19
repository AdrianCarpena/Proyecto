package com.agenda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.backend.model.User;
import com.agenda.backend.repository.BusyHoursRepository;
import com.agenda.backend.repository.ChatMembershipRepository;
import com.agenda.backend.repository.ChatRepository;
import com.agenda.backend.repository.ExamenRepository;
import com.agenda.backend.repository.MessageRepository;
import com.agenda.backend.repository.StudySessionRepository;
import com.agenda.backend.repository.TareaRepository;
import com.agenda.backend.repository.UserRepository;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private BusyHoursRepository busyHoursRepository;

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatMembershipRepository chatMembershipRepository;

    @Autowired
    private ChatRepository chatRepository;

    public void deleteOwnUser(User user) {

        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // agenda
        studySessionRepository.deleteByUser(existingUser);
        busyHoursRepository.deleteByUser(existingUser);
        examenRepository.deleteByUser(existingUser);
        tareaRepository.deleteByUser(existingUser);

        // chat
        messageRepository.deleteBySender(existingUser);
        chatMembershipRepository.deleteByUser(existingUser);
        chatRepository.deleteByCreator(existingUser);

        // usuario
        userRepository.delete(existingUser);
    }
}