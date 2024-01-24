package com.chatbot.models;

import com.chatbot.requests.MessageRequest;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Choice {
    private int index;

    private MessageRequest message;

    private Object logprobs;

    private String finish_reason;

}
