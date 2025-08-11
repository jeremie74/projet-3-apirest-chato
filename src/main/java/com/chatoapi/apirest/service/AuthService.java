package com.chatoapi.apirest.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chatoapi.apirest.dto.LoginRequest;
import com.chatoapi.apirest.dto.LoginResponse;
import com.chatoapi.apirest.model.User;
import com.chatoapi.apirest.repository.UserRepository;
import com.chatoapi.apirest.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe invalide"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe invalide");
        }

        String token = jwtService.generateToken(user); // génération du JWT
        return new LoginResponse(token);
    }
}
