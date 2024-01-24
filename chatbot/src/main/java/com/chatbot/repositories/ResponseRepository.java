package com.chatbot.repositories;


import com.chatbot.models.ResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
}
