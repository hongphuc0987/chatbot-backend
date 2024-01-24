package com.chatbot.responses;

import com.chatbot.models.Choice;
import com.chatbot.models.Usage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatGptResponse {
    private String id;

    private String object;

    private long created;

    private String model;

    private List<Choice> choices;

    private Usage usage;

    private String system_fingerprint;

}
