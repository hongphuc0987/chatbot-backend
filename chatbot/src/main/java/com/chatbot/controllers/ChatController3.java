package com.chatbot.controllers;

import com.chatbot.models.Choice;
import com.chatbot.models.ConversationEntity;
import com.chatbot.requests.MessageRequest;
import com.chatbot.models.MessageEntity;
import com.chatbot.repositories.ConversationRepository;
import com.chatbot.requests.ChatGptRequest;
import com.chatbot.responses.ChatGptResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.version.v1}/chat2")
public class ChatController3 {
    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Autowired
    private RestTemplate template;

    private final ConversationRepository conversationRepository;


    @PostMapping("")
    public ChatGptResponse chat (@Valid @RequestBody ChatGptRequest request){

        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);

        ConversationEntity conversation = new ConversationEntity();
        conversation.setIsActive(true);
        conversation.setCreatedAt(LocalDateTime.now());

        List<Choice> choices = chatGptResponse.getChoices();
        List<MessageEntity> messages = new ArrayList<>();

        for (Choice choice : choices) {
            String role = choice.getMessage().getRole();
            String content = choice.getMessage().getContent();

            MessageEntity messageSystem = new MessageEntity();
            messageSystem.setRole(role);
            messageSystem.setContent(content);
            messageSystem.setCreatedAt(LocalDateTime.now());
            messageSystem.setConversationId(conversation);

            messages.add(messageSystem);
        }
        conversation.setMessages(messages);
        conversationRepository.save(conversation);

        String latestUserQuestion = findLatestUserQuestion(conversation);

        if (latestUserQuestion != null) {
            request.getMessages().add(new MessageRequest("user", latestUserQuestion));
        }

        return chatGptResponse;

    }

    private String findLatestUserQuestion(ConversationEntity conversation) {
        List<MessageEntity> messages = conversation.getMessages();

        for (int i = messages.size() - 1; i >= 0; i--) {
            MessageEntity message = messages.get(i);
            if ("user".equals(message.getRole())) {
                return message.getContent();
            }
        }

        return null;
    }

}