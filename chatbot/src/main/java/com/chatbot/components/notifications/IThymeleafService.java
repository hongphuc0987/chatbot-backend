package com.chatbot.components.notifications;

import com.chatbot.models.UserEntity;

public interface IThymeleafService {
    String getVerifyContent(UserEntity user, String url);
}
