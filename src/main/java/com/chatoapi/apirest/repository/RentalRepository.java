package com.chatoapi.apirest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatoapi.apirest.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByName(String name);
}
