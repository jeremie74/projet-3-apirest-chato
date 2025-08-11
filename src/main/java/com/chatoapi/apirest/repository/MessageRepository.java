package com.chatoapi.apirest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chatoapi.apirest.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
