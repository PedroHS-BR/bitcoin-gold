package com.hidra.bitcoingold.mapper;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.UserPostDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract User toUser(UserPostDto userPostDto);
}
