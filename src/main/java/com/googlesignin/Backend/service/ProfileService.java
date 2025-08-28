package com.googlesignin.Backend.service;

import com.googlesignin.Backend.entity.Users;
import com.googlesignin.Backend.mapper.MapToProfileResponseDto;
import com.googlesignin.Backend.models.response.ProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final MapToProfileResponseDto mapToProfileResponseDto;
    public ProfileResponseDto viewUser(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Users principal = (Users) authentication.getPrincipal();

        return mapToProfileResponseDto.toProfileResponseDto(principal);
    }

}
