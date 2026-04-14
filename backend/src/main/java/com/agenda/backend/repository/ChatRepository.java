package com.agenda.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agenda.backend.model.Chat;
import com.agenda.backend.model.User;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	//Para poder buscar Chats mediante un código ( en el front puede transformarse en un qr)
    Optional<Chat> findByJoinCode(String joinCode);
    
    void deleteByCreator(User user);
}