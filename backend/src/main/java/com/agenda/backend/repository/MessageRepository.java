package com.agenda.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.backend.model.Chat;
import com.agenda.backend.model.Message;
import com.agenda.backend.model.User;

public interface MessageRepository extends JpaRepository<Message, Long> {

	//Para obtener los mensajes de un Chat en orden de publicacion
    List<Message> findByChatOrderBySentAtAsc(Chat chat);
    
    void deleteBySender(User user);
}