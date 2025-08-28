package com.googlesignin.Backend.mapper;

import com.googlesignin.Backend.entity.Users;
import com.googlesignin.Backend.models.response.ProfileResponseDto;
import org.springframework.stereotype.Component;

@Component
public class MapToProfileResponseDto {

    public ProfileResponseDto toProfileResponseDto(Users users){
        return ProfileResponseDto.builder()
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .email(users.getEmail())
                .build();
    }
}
