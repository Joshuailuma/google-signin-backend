package com.googlesignin.Backend.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    String message;
    String accessToken;
    String expiryTime;
}
