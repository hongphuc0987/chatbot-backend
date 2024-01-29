package com.chatbot.services;

import com.chatbot.components.security.UserPrincipal;
import com.chatbot.models.Choice;
import com.chatbot.models.ConversationEntity;
import com.chatbot.models.UserEntity;
import com.chatbot.repositories.MessageRepository;
import com.chatbot.requests.MessageRequest;
import com.chatbot.models.MessageEntity;
import com.chatbot.repositories.ConversationRepository;
import com.chatbot.requests.ChatGptRequest;
import com.chatbot.responses.ChatGptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService implements IChatService{

    @Autowired
    private RestTemplate template;

    @Autowired
    private ConversationRepository conversationRepository;


    @Autowired
    private MessageRepository messageRepository;
    @Value("${openai.model}")
    private String model;

    @Value(("${openai.api.url}"))
    private String apiURL;

    @Override
    public ChatGptResponse processChat(ChatGptRequest request, long conversationId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity user = userPrincipal.getUser();

        List<MessageRequest> allMessages = new ArrayList<>();

        ConversationEntity conversation ;
        if(conversationId==0){
            conversation = new ConversationEntity();
            conversation.setIsActive(true);
            conversation.setConversationName("New Conversation");
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setUserId(user);

        }else{
            conversation = conversationRepository.findById(conversationId);
            List<MessageEntity> oldMessages = messageRepository.findByConversationId(conversation);
            for (MessageEntity oldMessage : oldMessages) {
                MessageRequest messageRequest = new MessageRequest();
                messageRequest.setRole(oldMessage.getRole());
                messageRequest.setContent(oldMessage.getContent());
                allMessages.add(messageRequest);
            }

            allMessages.addAll(request.getMessages());
            request.setMessages(allMessages);
        }
        conversationRepository.save(conversation);

        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        List<Choice> choices = chatGptResponse.getChoices();
        List<MessageEntity> messages = new ArrayList<>();
        String latestUserQuestion = findLatestUserQuestion(request);

        if (latestUserQuestion != null) {
            MessageEntity userMessage = new MessageEntity();
            userMessage.setRole("user");
            userMessage.setContent(latestUserQuestion);
            userMessage.setCreatedAt(LocalDateTime.now());
            userMessage.setConversationId(conversation);

            messages.add(userMessage);
        }

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
        messageRepository.saveAll(messages);

        conversation.setMessages(messages);
        chatGptResponse.setConversationId(conversation.getId());

        return chatGptResponse;
    }
    private String findLatestUserQuestion(ChatGptRequest request) {
        List<MessageRequest> messages = request.getMessages();

        if (!messages.isEmpty()) {
            MessageRequest latestMessage = messages.get(messages.size() - 1);
            if ("user".equals(latestMessage.getRole())) {
                return latestMessage.getContent();
            }
        }
        return null;
    }



}

