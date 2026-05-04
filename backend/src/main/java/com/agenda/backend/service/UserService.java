package com.agenda.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.backend.dto.UserProfileDTO;
import com.agenda.backend.dto.UserProfileWithTokenDTO;
import com.agenda.backend.exception.ConflictException;
import com.agenda.backend.exception.ForbiddenException;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.BusyHoursRepository;
import com.agenda.backend.repository.ChatMembershipRepository;
import com.agenda.backend.repository.ChatRepository;
import com.agenda.backend.repository.ExamenRepository;
import com.agenda.backend.repository.MessageRepository;
import com.agenda.backend.repository.StudySessionRepository;
import com.agenda.backend.repository.TareaRepository;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.security.JwtUtils;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private JwtUtils jwtUtils;

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
    
    public UserProfileDTO getMyProfile(User user) {
        return new UserProfileDTO(user.getId(), user.getUsername());
    }

    public UserProfileWithTokenDTO changeUsername(User user, String newUsername) {

        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }

        if (newUsername.length() < 3) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres");
        }

        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new ConflictException("Ese nombre de usuario ya existe");
        }

        user.setUsername(newUsername);
        User saved = userRepository.save(user);

        String newToken = jwtUtils.generateToken(saved.getUsername());

        return new UserProfileWithTokenDTO(saved.getId(), saved.getUsername(), newToken);
    }

    public void changePassword(User user, String currentPassword, String newPassword) {

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Debes introducir la contraseña actual");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        if (newPassword.length() < 4) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 4 caracteres");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ForbiddenException("La contraseña actual no es correcta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}