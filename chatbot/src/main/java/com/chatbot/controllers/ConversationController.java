package com.chatbot.controllers;

import com.chatbot.models.ConversationEntity;
import com.chatbot.components.apis.CoreApiResponse;
import com.chatbot.services.IConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.version.v1}/conversation")
public class ConversationController {
    @Autowired
    private IConversationService conversationService;

    @PostMapping("update/{id}")
    public CoreApiResponse<ConversationEntity> renameConversation(@PathVariable Long id,@RequestBody String name ) {
        ConversationEntity conversation = conversationService.renameConversation(id,name);
        return CoreApiResponse.success(conversation);
    }
    @GetMapping("/all")
    public CoreApiResponse<List<ConversationEntity>>getAllConversation() {
        List<ConversationEntity> conversations= conversationService.getAllConversation();
        return CoreApiResponse.success(conversations);
    }


    @GetMapping("/{id}")
    public CoreApiResponse<ConversationEntity>getConversation(@PathVariable Long id ) {
        ConversationEntity conversation = conversationService.getConversationById(id);
        return CoreApiResponse.success(conversation);
    }



    @DeleteMapping("/{id}")
    public CoreApiResponse<?> deleteConversation(@PathVariable Long id){
        ConversationEntity conversation = conversationService.deleteConversation(id);
        return CoreApiResponse.success("Delete coversation successfully");
    }
    @GetMapping("/user")
    public CoreApiResponse<List<ConversationEntity>>getConversationByUser() {
        List<ConversationEntity> conversations = conversationService.getConversationByUser();
        return CoreApiResponse.success(conversations);
    }


}
