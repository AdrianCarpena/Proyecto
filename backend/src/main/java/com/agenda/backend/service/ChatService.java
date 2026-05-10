package com.agenda.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.dto.ChatJoinCodeDTO;
import com.agenda.backend.dto.ChatMemberDTO;
import com.agenda.backend.dto.ChatResponseDTO;
import com.agenda.backend.dto.CreateChatRequestDTO;
import com.agenda.backend.dto.JoinChatRequestDTO;
import com.agenda.backend.exception.ForbiddenException;
import com.agenda.backend.model.Chat;
import com.agenda.backend.model.ChatMembership;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.ChatMembershipRepository;
import com.agenda.backend.repository.ChatRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMembershipRepository chatMembershipRepository;

    //Metodo para crear un Chat, donde el propietario será el que lo crea y se generara un codigo para unirse
    public ChatResponseDTO createChat(CreateChatRequestDTO request, User user) {

        Chat chat = new Chat();
        chat.setNombre(request.getNombre());
        chat.setJoinCode(generateJoinCode());
        chat.setCreator(user);
        chat.setCreatedAt(LocalDateTime.now());

        Chat savedChat = chatRepository.save(chat);

        ChatMembership membership = new ChatMembership();
        membership.setChat(savedChat);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());

        chatMembershipRepository.save(membership);

        return toChatResponseDTO(savedChat, 1);
    }

    //Metodo para unirse al chat mediante el codigo
    public ChatResponseDTO joinChat(JoinChatRequestDTO request, User user) {

        Chat chat = chatRepository.findByJoinCode(request.getJoinCode())
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        boolean alreadyMember = chatMembershipRepository.findByChatAndUser(chat, user).isPresent();

        if (alreadyMember) {
            throw new IllegalArgumentException("Ya perteneces a este chat");
        }

        ChatMembership membership = new ChatMembership();
        membership.setChat(chat);
        membership.setUser(user);
        membership.setJoinedAt(LocalDateTime.now());

        chatMembershipRepository.save(membership);

        int memberCount = chatMembershipRepository.findByChat(chat).size();

        return toChatResponseDTO(chat, memberCount);
    }

    //Metodo para listar todos los chats que tiene un usuario
    public List<ChatResponseDTO> getUserChats(User user) {

        List<ChatMembership> memberships = chatMembershipRepository.findByUser(user);

        return memberships.stream()
                .map(membership -> {
                    Chat chat = membership.getChat();
                    int memberCount = chatMembershipRepository.findByChat(chat).size();
                    return toChatResponseDTO(chat, memberCount);
                })
                .toList();
    }

    //Metodo para obtener los miembros de un chat
    public List<ChatMemberDTO> getChatMembers(Long chatId, User user) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ensureUserIsMember(chat, user);

        List<ChatMembership> memberships = chatMembershipRepository.findByChat(chat);

        return memberships.stream()
                .map(membership -> new ChatMemberDTO(
                        membership.getUser().getId(),
                        membership.getUser().getUsername(),
                        membership.getJoinedAt()
                ))
                .toList();
    }

    //Metodo para borrar a un usuario de un chat
    public void removeMember(Long chatId, Long memberId, User user) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        if (!chat.getCreator().getId().equals(user.getId())) {
            throw new ForbiddenException("Solo el creador del chat puede expulsar miembros");
        }

        ChatMembership membership = chatMembershipRepository.findByChatIdAndUserId(chatId, memberId)
                .orElseThrow(() -> new RuntimeException("Miembro no encontrado en el chat"));

        User memberToRemove = membership.getUser();

        if (chat.getCreator().getId().equals(memberId)) {
            throw new IllegalArgumentException("El creador no puede expulsarse a sí mismo");
        }

        chatMembershipRepository.deleteByChatAndUser(chat, memberToRemove);
    }

    //Metodo para comprobar si un user pertenece a un chat
    private void ensureUserIsMember(Chat chat, User user) {
        boolean isMember = chatMembershipRepository.findByChatAndUser(chat, user).isPresent();

        if (!isMember) {
            throw new ForbiddenException("No perteneces a este chat");
        }
    }

    //Metodo para generar el codigo de un chat
    private String generateJoinCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    //Metodo para obtener el codigo de union de un chat
    public ChatJoinCodeDTO getJoinCode(Long chatId, User user) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ensureUserIsMember(chat, user);

        return new ChatJoinCodeDTO(chat.getId(), chat.getJoinCode());
    }
    
    //Metodo para abandonar un chat
    public void leaveChat(Long chatId, User user) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ChatMembership membership = chatMembershipRepository.findByChatAndUser(chat, user)
                .orElseThrow(() -> new RuntimeException("No perteneces a este chat"));

        // Si el que abandona es el creador, se elimina el chat completo
        if (chat.getCreator().getId().equals(user.getId())) {
            chatRepository.delete(chat);
            return;
        }

        // Si es miembro normal, solo se elimina su relación con el chat
        chatMembershipRepository.delete(membership);
    }

    //Metodo para transformar un chat y su numero de miembros a un objeto del tipo ChatResponseDTO
    private ChatResponseDTO toChatResponseDTO(Chat chat, int memberCount) {
        return new ChatResponseDTO(
                chat.getId(),
                chat.getNombre(),
                chat.getJoinCode(),
                chat.getCreator().getId(),
                chat.getCreator().getUsername(),
                memberCount,
                chat.getCreatedAt()
        );
    }
    
}