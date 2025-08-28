package com.googlesignin.Backend.exception;

import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.naming.AuthenticationException;


@Getter
@Setter
@NoArgsConstructor
public class CustomAuthenticationException extends AuthenticationException {

    public CustomAuthenticationException(String message) {
    }
}
