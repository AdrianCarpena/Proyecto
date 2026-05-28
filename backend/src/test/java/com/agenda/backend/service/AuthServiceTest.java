package com.agenda.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agenda.backend.exception.ConflictException;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("Rober");
        existingUser.setPassword("1234");
    }

    @Test
    void noDebeRegistrarUsuarioExistente() {

        when(userRepository.findByUsername("Rober"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> {
            authService.register("Rober", "1234");
        });

        verify(userRepository, never()).save(any(User.class));
    }
}