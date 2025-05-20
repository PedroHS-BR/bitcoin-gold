package com.hidra.bitcoingold.mapper;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.UserPostRequest;
import com.hidra.bitcoingold.dtos.UserResponse;
import com.hidra.bitcoingold.dtos.UserUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract User toUser(UserPostRequest userPostRequest);

    public abstract User toUser(UserUpdateRequest userUpdateRequest);

    public abstract List<UserResponse> toUserResponseList(List<User> user);

    public abstract UserResponse toUserResponse(User user);

}
