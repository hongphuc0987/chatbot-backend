package com.chatbot.services;

import com.chatbot.models.UserEntity;
import com.chatbot.requests.SignInRequest;
import com.chatbot.requests.SignUpRequest;
import com.chatbot.responses.SignInResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    void signUp(SignUpRequest request);

    void verify(Long userId,String token);

    SignInResponse signIn(SignInRequest request);

}
