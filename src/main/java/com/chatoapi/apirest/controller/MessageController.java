package com.chatoapi.apirest.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatoapi.apirest.dto.ApiMessage;
import com.chatoapi.apirest.dto.MessageCreateRequest;
import com.chatoapi.apirest.model.Message;
import com.chatoapi.apirest.model.Rental;
import com.chatoapi.apirest.model.User;
import com.chatoapi.apirest.repository.MessageRepository;
import com.chatoapi.apirest.repository.RentalRepository;
import com.chatoapi.apirest.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @PostMapping({ "", "/" })
    public ResponseEntity<ApiMessage> sendMessage(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody MessageCreateRequest req) {

        // 1) Utilisateur courant (depuis le JWT)
        User current = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 2) Anti-usurpation : le user_id envoyé doit être celui du token
        if (!current.getId().equals(req.userId())) {
            return ResponseEntity.status(403).body(new ApiMessage("Forbidden"));
        }

        // 3) Vérifier l'annonce
        Rental rental = rentalRepository.findById(req.rentalId())
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        // 4) Sauvegarder
        Message m = new Message();
        m.setSender(current);
        m.setRental(rental);
        m.setContent(req.message());
        m.setCreatedAt(LocalDateTime.now());
        m.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(m);

        // 5) Réponse Mockoon-compatible
        return ResponseEntity.ok(new ApiMessage("Message send with success"));
    }
}
