package com.agenda.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agenda.backend.dto.SendMessageRequestDTO;
import com.agenda.backend.exception.ForbiddenException;
import com.agenda.backend.model.Chat;
import com.agenda.backend.model.Message;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.ChatMembershipRepository;
import com.agenda.backend.repository.ChatRepository;
import com.agenda.backend.repository.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatMembershipRepository chatMembershipRepository;

    @InjectMocks
    private MessageService messageService;

    private User user;
    private Chat chat;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Rober");

        chat = new Chat();
        chat.setId(1L);
        chat.setNombre("Chat DAM");
        chat.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void usuarioNoMiembroNoPuedeEnviarMensaje() {
        SendMessageRequestDTO request = new SendMessageRequestDTO();
        request.setContent("Hola");

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(chatMembershipRepository.findByChatAndUser(chat, user)).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> {
            messageService.sendMessage(1L, request, user);
        });

        verify(messageRepository, never()).save(any(Message.class));
    }
}