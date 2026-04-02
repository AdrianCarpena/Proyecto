package com.agenda.backend.service;

import com.agenda.backend.exception.ConflictException;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.UserRepository;
import com.agenda.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    // Registro de usuario
    public User register(String username, String password) {
        if(userRepository.findByUsername(username).isPresent()){
        	throw new ConflictException("El usuario ya existe");
        }

        User user = new User();
        user.setUsername(username);
        // guardamos la contraseña hasheada
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    // Login
    public String login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if(userOpt.isEmpty()){
        	throw new ConflictException("Credenciales incorrectas");
        }

        User user = userOpt.get();

        // Comprobamos contraseña
        if(!passwordEncoder.matches(password, user.getPassword())){
        	throw new ConflictException("Credenciales incorrectas");
        }

        // Generamos JWT
        return jwtUtils.generateToken(user.getUsername());
    }
}