package com.chatbot.components.notifications;

import com.chatbot.components.events.MailEvent;

public interface IMailService {
    void sendMail(MailEvent event);
}

