package com.chatbot.services;

import com.chatbot.components.exceptions.DataNotFoundException;
import com.chatbot.components.security.UserPrincipal;
import com.chatbot.models.ConversationEntity;
import com.chatbot.models.UserEntity;
import com.chatbot.repositories.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.chatbot.components.exceptions.AppException;

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
    public List<ConversationEntity> getConversationByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();
        return conversationRepository.findByUserId(user.getId());
    }


    @Override
    public ConversationEntity deleteConversation(Long id) {
        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Conversation", "id", id));
        conversation.setIsActive(false);
        conversationRepository.save(conversation);
        return null;
    }

    @Override
    public ConversationEntity getConversationById(Long id) {
        return conversationRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.BAD_REQUEST, "Cannot find conversation with id: " + id));
    }
}
