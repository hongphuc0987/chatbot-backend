package com.chatbot.controllers;


import com.chatbot.requests.ChatGptRequest;
import com.chatbot.responses.ChatGptResponse;
import com.chatbot.services.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.version.v1}/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("")
    public ChatGptResponse chat (@Valid @RequestParam(required = false) Long id,
                                 @RequestBody ChatGptRequest request) {
        return chatService.processChat(request, id);
    }


}