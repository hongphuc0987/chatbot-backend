package com.chatbot.requests;

import com.chatbot.models.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String conversationName;

}
