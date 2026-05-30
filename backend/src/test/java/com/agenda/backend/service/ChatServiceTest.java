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

import com.agenda.backend.exception.ForbiddenException;
import com.agenda.backend.model.Chat;
import com.agenda.backend.model.ChatMembership;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.ChatMembershipRepository;
import com.agenda.backend.repository.ChatRepository;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatMembershipRepository chatMembershipRepository;

    @InjectMocks
    private ChatService chatService;

    private User creator;
    private User otherUser;
    private Chat chat;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setUsername("Creador");

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("Otro");

        chat = new Chat();
        chat.setId(1L);
        chat.setNombre("Chat DAM");
        chat.setCreator(creator);
        chat.setJoinCode("ABC123");
        chat.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void usuarioNoMiembroNoPuedeVerMiembrosDelChat() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(chatMembershipRepository.findByChatAndUser(chat, otherUser)).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class, () -> {
            chatService.getChatMembers(1L, otherUser);
        });
    }

    @Test
    void usuarioNoCreadorNoPuedeExpulsarMiembros() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        assertThrows(ForbiddenException.class, () -> {
            chatService.removeMember(1L, 1L, otherUser);
        });

        verify(chatMembershipRepository, never()).delete(any(ChatMembership.class));
    }
}