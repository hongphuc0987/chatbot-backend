package com.chatbot.responses;

import lombok.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInResponse {
    private String refreshToken;

    private String accessToken;

    private Long userId;

    private String fullName;


}

