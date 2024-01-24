package com.chatbot.services;

import com.chatbot.requests.ChatGptRequest;
import com.chatbot.responses.ChatGptResponse;

public interface IChatService {
    ChatGptResponse processChat(ChatGptRequest request, long conversationId);


}
