package com.agenda.backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.agenda.backend.dto.MessageResponseDTO;
import com.agenda.backend.dto.SendMessageRequestDTO;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.service.MessageService;

@Controller
public class ChatMessageWebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats/{chatId}/send")
    public void sendMessage(@DestinationVariable Long chatId,
                            SendMessageRequestDTO request,
                            Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MessageResponseDTO savedMessage = messageService.sendMessage(chatId, request, user);

        messagingTemplate.convertAndSend("/topic/chats/" + chatId, savedMessage);
    }
}