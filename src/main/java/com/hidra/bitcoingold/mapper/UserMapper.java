package com.hidra.bitcoingold.mapper;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    public static final UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract User toUser(UserPostRequest userPostRequest);

    public abstract User toUser(UserUpdateRequest userUpdateRequest);

    public abstract User toUser(RegisterUserPostRequest commonUserPostRequest);

    public abstract User toUser(RegularUserUpdateRequest regularUserUpdateRequest);

    public abstract List<UserResponse> toUserResponseList(List<User> user);

    public abstract UserResponse toUserResponse(User user);

    @Mapping(target = "balance", ignore = true)
    public abstract UserDataResponse toUserDataResponse(User user);

}
