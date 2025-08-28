package com.googlesignin.Backend.controller;

import com.googlesignin.Backend.models.request.GoogleLoginRequestDto;
import com.googlesignin.Backend.models.request.LoginRequestDto;
import com.googlesignin.Backend.models.response.LoginResponseDto;
import com.googlesignin.Backend.models.request.RegistrationRequestDto;
import com.googlesignin.Backend.models.response.RegistrationResponseDto;
import com.googlesignin.Backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody RegistrationRequestDto registrationRequestDto){
       RegistrationResponseDto registrationResponseDto = authenticationService.register(registrationRequestDto);
    return  ResponseEntity.status(HttpStatus.CREATED).body(registrationResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponse = authenticationService.login(loginRequestDto);
        return  ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @PostMapping("/google-login")
    public ResponseEntity<LoginResponseDto> googleLogin(@RequestBody GoogleLoginRequestDto googleLoginRequestDto){
        LoginResponseDto loginResponse = authenticationService.login(googleLoginRequestDto);
        return  ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
