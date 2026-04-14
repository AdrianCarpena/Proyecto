package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.MessageResponseDTO;
import com.agenda.backend.dto.SendMessageRequestDTO;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chats/{chatId}/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Ver mensajes del chat, el usuario debe pertenecer al chat
    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getChatMessages(@PathVariable Long chatId, Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(messageService.getChatMessages(chatId, user));
    }

    // Enviar mensaje, el usuario debe pertenecer al chat
    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(@PathVariable Long chatId,
                                                          @Valid @RequestBody SendMessageRequestDTO request,
                                                          Principal principal) {
        User user = getUser(principal);
        MessageResponseDTO response = messageService.sendMessage(chatId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}