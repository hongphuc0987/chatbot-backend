package com.chatbot.services;

import com.chatbot.models.ConversationEntity;

public interface IConversationService {
    ConversationEntity createConversation (ConversationEntity conversation);

    ConversationEntity deleteConversation(Long id);
}
