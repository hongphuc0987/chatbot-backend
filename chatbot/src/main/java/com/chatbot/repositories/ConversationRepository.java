package com.chatbot.repositories;

import com.chatbot.models.ConversationEntity;
import com.chatbot.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {

    ConversationEntity findById(long id );

    @Query("SELECT c FROM ConversationEntity c WHERE c.userId.id = :userId")
    List<ConversationEntity> findByUserId(@Param("userId") Long userId);


}
