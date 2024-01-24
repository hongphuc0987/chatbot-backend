package com.chatbot.services;

import com.chatbot.components.exceptions.DataNotFoundException;
import com.chatbot.models.ConversationEntity;
import com.chatbot.repositories.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
@Service
public class ConversationService implements IConversationService{
    @Autowired
    private ConversationRepository conversationRepository;


    @Override
    public ConversationEntity renameConversation(Long id,  String name ) {
        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Conversation", "id", id));
        conversation.setConversationName(name);
        conversationRepository.save(conversation);
        return conversation;
    }

    @Override
    public List<ConversationEntity> getAllConversation(){
        return conversationRepository.findAll();
    }

    @Override
    public ConversationEntity deleteConversation(Long id) {
        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Conversation", "id", id));
        conversation.setIsActive(false);
        conversationRepository.save(conversation);
        return null;
    }
}
