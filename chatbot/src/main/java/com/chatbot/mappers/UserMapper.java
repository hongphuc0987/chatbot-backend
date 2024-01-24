package com.chatbot.mappers;

import com.chatbot.models.UserEntity;
import com.chatbot.requests.SignUpRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserEntity toModel(SignUpRequest request);
}
