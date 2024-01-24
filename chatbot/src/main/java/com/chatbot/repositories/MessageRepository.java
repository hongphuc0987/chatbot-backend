package com.chatbot.repositories;

import com.chatbot.models.ConversationEntity;
import com.chatbot.models.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    @Query("SELECT m FROM MessageEntity m WHERE m.conversationId = :conversation")
    List<MessageEntity> findByConversationId(@Param("conversation") ConversationEntity conversation);
}
