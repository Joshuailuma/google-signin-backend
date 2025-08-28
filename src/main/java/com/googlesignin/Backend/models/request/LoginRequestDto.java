package com.googlesignin.Backend.models.request;

public record LoginRequestDto(
        String email,
        String password
) {
}
