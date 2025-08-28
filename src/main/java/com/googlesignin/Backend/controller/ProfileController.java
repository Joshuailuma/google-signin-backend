package com.googlesignin.Backend.controller;

import com.googlesignin.Backend.models.response.ProfileResponseDto;
import com.googlesignin.Backend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    @GetMapping
    public ResponseEntity<ProfileResponseDto> profile(){


    return  ResponseEntity.status(HttpStatus.OK).body(profileService.viewUser());
    }
}
