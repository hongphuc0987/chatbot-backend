package com.chatbot.repositories;

import com.chatbot.models.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {

    ConversationEntity findById(long id );


}
