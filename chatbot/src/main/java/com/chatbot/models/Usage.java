package com.chatbot.models;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usage {
    private int prompt_tokens;

    private int completion_tokens;

    private int total_tokens;
}
