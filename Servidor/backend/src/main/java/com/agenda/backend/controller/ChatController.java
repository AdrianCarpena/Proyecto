package com.agenda.backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.agenda.backend.dto.ChatJoinCodeDTO;
import com.agenda.backend.dto.ChatMemberDTO;
import com.agenda.backend.dto.ChatResponseDTO;
import com.agenda.backend.dto.CreateChatRequestDTO;
import com.agenda.backend.dto.JoinChatRequestDTO;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.service.ChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Crear chat
    @PostMapping
    public ResponseEntity<ChatResponseDTO> createChat(@Valid @RequestBody CreateChatRequestDTO request,
                                                      Principal principal) {
        User user = getUser(principal);
        ChatResponseDTO response = chatService.createChat(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Unirse a chat con joinCode
    @PostMapping("/join")
    public ResponseEntity<ChatResponseDTO> joinChat(@Valid @RequestBody JoinChatRequestDTO request,
                                                    Principal principal) {
        User user = getUser(principal);
        ChatResponseDTO response = chatService.joinChat(request, user);
        return ResponseEntity.ok(response);
    }

    // Ver chats del usuario
    @GetMapping
    public ResponseEntity<List<ChatResponseDTO>> getUserChats(Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(chatService.getUserChats(user));
    }

    // Ver miembros de un chat
    @GetMapping("/{chatId}/members")
    public ResponseEntity<List<ChatMemberDTO>> getChatMembers(@PathVariable Long chatId,
                                                              Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(chatService.getChatMembers(chatId, user));
    }

    // Expulsar miembro
    @DeleteMapping("/{chatId}/members/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long chatId,
                                          @PathVariable Long memberId,
                                          Principal principal) {
        User user = getUser(principal);
        chatService.removeMember(chatId, memberId, user);
        return ResponseEntity.ok("Miembro expulsado correctamente");
    }
    
    @GetMapping("/{chatId}/join-code")
    public ResponseEntity<ChatJoinCodeDTO> getJoinCode(@PathVariable Long chatId,
                                                       Principal principal) {
        User user = getUser(principal);
        return ResponseEntity.ok(chatService.getJoinCode(chatId, user));
    }
}