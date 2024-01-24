package com.chatbot.controllers;


import com.chatbot.requests.ChatGptRequest;
import com.chatbot.responses.ChatGptResponse;
import com.chatbot.services.ChatService;
import com.chatbot.services.IChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.version.v1}/chat")
public class ChatController {

    @Autowired
    private IChatService chatService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public ChatGptResponse chat(
            @Valid @RequestParam(defaultValue = "0") Long id,
            @RequestBody ChatGptRequest request) {
        return chatService.processChat(request, id);
    }

}