package com.chatoapi.apirest.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chatoapi.apirest.dto.RentalCreateRequest;
import com.chatoapi.apirest.dto.RentalResponse;
import com.chatoapi.apirest.dto.RentalUpdateRequest;
import com.chatoapi.apirest.dto.RentalsListResponse;
import com.chatoapi.apirest.model.Rental;
import com.chatoapi.apirest.model.User;
import com.chatoapi.apirest.repository.RentalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public RentalsListResponse listAll() {
        List<RentalResponse> items = rentalRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
        return new RentalsListResponse(items);
    }

    public RentalResponse getOne(Long id) {
        Rental r = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        return toResponse(r);
    }

    @Transactional
    public RentalResponse create(User owner, RentalCreateRequest req) {
        Rental r = new Rental();
        r.setName(req.name());
        r.setPicture(req.picture());
        r.setSurface(req.surface());
        r.setPrice(req.price());
        r.setDescription(req.description());
        r.setOwner(owner);
        r.setCreatedAt(java.time.LocalDateTime.now());
        r.setUpdatedAt(java.time.LocalDateTime.now());
        rentalRepository.save(r);
        return toResponse(r);
    }

    @Transactional
    public RentalResponse update(Long id, User currentUser, RentalUpdateRequest req) {
        Rental r = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        // Ownership check
        if (!r.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("Forbidden: not the owner");
        }

        r.setName(req.name());
        r.setPicture(req.picture());
        r.setSurface(req.surface());
        r.setPrice(req.price());
        r.setDescription(req.description());
        r.setUpdatedAt(java.time.LocalDateTime.now());
        rentalRepository.save(r);
        return toResponse(r);
    }

    private RentalResponse toResponse(Rental r) {
        return new RentalResponse(
                r.getId(),
                r.getName(),
                r.getSurface(),
                r.getPicture(),
                r.getDescription(),
                r.getCreatedAt() != null ? r.getCreatedAt().format(ISO) : null,
                r.getUpdatedAt() != null ? r.getUpdatedAt().format(ISO) : null,
                r.getOwner() != null ? r.getOwner().getId() : null,
                r.getPrice() != null ? r.getPrice().toPlainString() : null);
    }
}
