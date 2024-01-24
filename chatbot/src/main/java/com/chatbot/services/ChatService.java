package com.chatbot.services;

import com.chatbot.models.Choice;
import com.chatbot.models.ConversationEntity;
import com.chatbot.repositories.MessageRepository;
import com.chatbot.requests.MessageRequest;
import com.chatbot.models.MessageEntity;
import com.chatbot.repositories.ConversationRepository;
import com.chatbot.requests.ChatGptRequest;
import com.chatbot.responses.ChatGptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

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

    public ChatGptResponse processChat(ChatGptRequest request, long conversationId){

        ChatGptResponse chatGptResponse = template.postForObject(apiURL, request, ChatGptResponse.class);
        ConversationEntity conversation = conversationRepository.findById(conversationId);
        if(conversationId==0){
            conversation = new ConversationEntity();
            conversation.setIsActive(true);
            conversation.setCreatedAt(LocalDateTime.now());

        }

        List<MessageEntity> oldMessages = messageRepository.findByConversationId(conversation);

        List<MessageRequest> allMessages = new ArrayList<>();
        for (MessageEntity oldMessage : oldMessages) {
            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setRole(oldMessage.getRole());
            messageRequest.setContent(oldMessage.getContent());
            allMessages.add(messageRequest);
        }

        allMessages.addAll(request.getMessages());

        request.setMessages(allMessages);


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

        conversation.setMessages(messages);
        conversationRepository.save(conversation);

        return chatGptResponse;


    }



    private String findLatestUserQuestion(ChatGptRequest request) {
        List<MessageRequest> messages = request.getMessages();

        for (int i = messages.size() - 1; i >= 0; i--) {
            MessageRequest message = messages.get(i);
            if ("user".equals(message.getRole())) {
                return message.getContent();
            }
        }

        return null; // Không tìm thấy câu hỏi từ người dùng
    }


}

