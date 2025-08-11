package com.chatoapi.apirest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatoapi.apirest.dto.LoginRequest;
import com.chatoapi.apirest.dto.LoginResponse;
import com.chatoapi.apirest.dto.RegisterRequest;
import com.chatoapi.apirest.dto.UserResponse;
import com.chatoapi.apirest.model.User;
import com.chatoapi.apirest.repository.UserRepository;
import com.chatoapi.apirest.service.AuthService;
import com.chatoapi.apirest.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req) {
        User u = userService.registerUser(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UserResponse(
                        u.getId(), u.getName(), u.getEmail(),
                        String.valueOf(u.getCreatedAt()), String.valueOf(u.getUpdatedAt())));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> ResponseEntity.ok(toResponse(user)))
                .orElse(ResponseEntity.status(404).build());
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt().toString(),
                user.getUpdatedAt().toString());
    }

}
