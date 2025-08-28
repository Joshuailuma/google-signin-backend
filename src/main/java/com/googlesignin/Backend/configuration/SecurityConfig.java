package com.googlesignin.Backend.configuration;

import com.googlesignin.Backend.exception.AuthenticationExceptionHandler;
import com.googlesignin.Backend.exception.AccessDeniedExceptionHandler;
import com.googlesignin.Backend.security.JWTFilterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTFilterConfig jwtFilterConfig;
    private final AuthenticationExceptionHandler authenticationExceptionHandler;
    private final AccessDeniedExceptionHandler accessDeniedExceptionHandler;
@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{

        httpSecurity.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/authentication/register", "/authentication/login",  "/authentication/google-login").permitAll()
                .anyRequest().authenticated()
        )
                .exceptionHandling(request -> {
                    request.authenticationEntryPoint(authenticationExceptionHandler);
                    request.accessDeniedHandler(accessDeniedExceptionHandler);
                })

                .sessionManagement(
                        (session)
                                -> session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilterConfig, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(CorsConfigurer::disable);

       return httpSecurity.build();
    }

}
