package com.chatoapi.apirest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chatoapi.apirest.dto.RentalCreateRequest;
import com.chatoapi.apirest.dto.RentalResponse;
import com.chatoapi.apirest.dto.RentalUpdateRequest;
import com.chatoapi.apirest.dto.RentalsListResponse;
import com.chatoapi.apirest.model.User;
import com.chatoapi.apirest.repository.UserRepository;
import com.chatoapi.apirest.service.RentalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
@Validated
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    // GET /api/rentals
    @GetMapping
    public ResponseEntity<RentalsListResponse> list() {
        return ResponseEntity.ok(rentalService.listAll());
    }

    // GET /api/rentals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RentalResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getOne(id));
    }

    // POST /api/rentals (auth requis)
    @PostMapping
    public ResponseEntity<MessageResponse> create(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody RentalCreateRequest req) {

        User owner = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        rentalService.create(owner, req);
        return ResponseEntity.ok(new MessageResponse("Rental created !"));
    }

    // PUT /api/rentals/{id} (seul le propriétaire)
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody RentalUpdateRequest req) {

        User current = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            rentalService.update(id, current, req);
            return ResponseEntity.ok(new MessageResponse("Rental updated !"));
        } catch (SecurityException se) {
            // Pas propriétaire
            return ResponseEntity.status(403).body(new MessageResponse("Forbidden"));
        }
    }

    public record MessageResponse(String message) {
    }
}
