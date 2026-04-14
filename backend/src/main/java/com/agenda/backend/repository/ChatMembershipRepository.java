package com.agenda.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.backend.model.Chat;
import com.agenda.backend.model.ChatMembership;
import com.agenda.backend.model.User;

public interface ChatMembershipRepository extends JpaRepository<ChatMembership, Long> {

	//Para ver en que Chats está un user
    List<ChatMembership> findByUser(User user);

    //Para ver que usuarios hay en un Chat
    List<ChatMembership> findByChat(Chat chat);

    //Para ver si un user esta en un chat
    Optional<ChatMembership> findByChatAndUser(Chat chat, User user);

    //Para borrar a un user de un Chat
    void deleteByChatAndUser(Chat chat, User user);
    
    //Metodo para buscar un objeto ChatMembership a traves de un chat y user concretos.
    Optional<ChatMembership> findByChatIdAndUserId(Long chatId, Long userId);
    
    void deleteByUser(User user);
}