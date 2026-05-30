package com.agenda.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.backend.dto.MessageResponseDTO;
import com.agenda.backend.dto.SendMessageRequestDTO;
import com.agenda.backend.exception.ForbiddenException;
import com.agenda.backend.model.Chat;
import com.agenda.backend.model.ChatMembership;
import com.agenda.backend.model.Message;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.ChatMembershipRepository;
import com.agenda.backend.repository.ChatRepository;
import com.agenda.backend.repository.MessageRepository;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMembershipRepository chatMembershipRepository;

    
    //Metodo que devuelve en forma de dto los mensajes de un chat en orden de fecha ascendente, solo en caso de que el user pertenezca al chat y este exista
    public List<MessageResponseDTO> getChatMessages(Long chatId, User user) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ensureUserIsMember(chat, user);

        return messageRepository.findByChatOrderBySentAtAsc(chat)
                .stream()
                .map(this::toMessageResponseDTO)
                .toList();
    }

    
    //Metodo para enviar un mensaje, comprueba que existe el chat y que el user pertenece a este, después crea el mensaje
    public MessageResponseDTO sendMessage(Long chatId, SendMessageRequestDTO request, User user) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ensureUserIsMember(chat, user);

        Message message = new Message();
        message.setChat(chat);
        message.setSender(user);
        message.setContent(request.getContent());
        message.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        return toMessageResponseDTO(saved);
    }

    //Metodo para poder comprobar que un user pertenezca a un chat, esto es para que usuarios que no esten en un chat no puedan leer ni escribir.
    private void ensureUserIsMember(Chat chat, User user) {
        ChatMembership membership = chatMembershipRepository.findByChatAndUser(chat, user)
                .orElseThrow(() -> new ForbiddenException("No perteneces a este chat"));
    }

    //Metodo para transformar un objeto message a su dto.
    private MessageResponseDTO toMessageResponseDTO(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getContent(),
                message.getChat().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getSentAt()
        );
    }
}