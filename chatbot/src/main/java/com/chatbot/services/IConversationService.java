package com.chatbot.services;

import com.chatbot.models.ConversationEntity;

import java.util.List;

public interface IConversationService {
    ConversationEntity renameConversation (Long id, String name);

    ConversationEntity deleteConversation(Long id);

    List<ConversationEntity> getAllConversation();
}
