package com.googlesignin.Backend.service;

import com.googlesignin.Backend.entity.Users;
import com.googlesignin.Backend.models.request.GoogleLoginRequestDto;
import com.googlesignin.Backend.models.request.LoginRequestDto;
import com.googlesignin.Backend.models.request.RegistrationRequestDto;
import com.googlesignin.Backend.models.response.LoginResponseDto;
import com.googlesignin.Backend.models.response.RegistrationResponseDto;
import com.googlesignin.Backend.repository.UsersRepository;
import com.googlesignin.Backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public RegistrationResponseDto register(RegistrationRequestDto registrationRequestDto) {
        usersRepository.findByEmail(registrationRequestDto.email())
                .ifPresent(users -> {
                    throw new RuntimeException("User having that email exists");
                });
       Users user = Users.builder()
                .email(registrationRequestDto.email())
                .firstName(registrationRequestDto.firstName())
                .lastName(registrationRequestDto.lastName())
                .username(registrationRequestDto.email())
               .password(passwordEncoder.encode(registrationRequestDto.password()))
                .build();

        usersRepository.save(user);

        return RegistrationResponseDto.builder()
                .message("Registration successful")
                .build();
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

        Users user = usersRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(()-> new RuntimeException("User not found"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password())
        );

        String jwtToken = jwtService.generateToken(user);
        String expiryTime = String.valueOf(jwtService.getJwtExpiration());
        jwtService.saveToken(user, jwtToken);

        return LoginResponseDto.builder()
                .message("Login successful")
                .accessToken(jwtToken)
                .expiryTime(expiryTime)
                .build();
    }

    public LoginResponseDto login(GoogleLoginRequestDto googleLoginRequestDto) {
        return jwtService.verifyGoogleToken(googleLoginRequestDto.idToken());
    }



    }
