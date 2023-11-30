package com.example.demo.security.Handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationAccessDinedHandler implements AccessDeniedHandler {

    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String deniedUrl = errorPage + "?exception = " + accessDeniedException.getMessage();
        response.sendRedirect(deniedUrl);

    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }


}
