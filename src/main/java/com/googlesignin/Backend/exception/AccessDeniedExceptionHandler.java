package com.googlesignin.Backend.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.io.PrintWriter;
@Configuration
public class AccessDeniedExceptionHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String message = "No permission to access this resource";

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write("{" +
                "\"errorMessage :\"" + message+

                "}");
        writer.flush();
    }
}
