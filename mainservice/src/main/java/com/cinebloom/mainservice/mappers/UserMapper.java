package com.cinebloom.mainservice.mappers;

import com.cinebloom.mainservice.domain.User;
import com.cinebloom.mainservice.dtos.UserProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "profilePicture", ignore = true) // handled manually
    UserProfileDTO toDto(User user);

    @Mapping(target = "profilePicture", ignore = true)
    User toUser(UserProfileDTO dto);
}