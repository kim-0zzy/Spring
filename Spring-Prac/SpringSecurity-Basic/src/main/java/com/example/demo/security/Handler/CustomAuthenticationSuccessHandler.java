package com.example.demo.security.Handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 사용자가 요청했던 정보를 담고있는 객체,
        // null 일 수도 있음 언제? 이 전의 정보가 없을 경우
        // ex) 다른 페이지에 있다가 로그인 요청 시도했을 때 원래 있던 url, 인증 전 다른 자원 접근 후 인증예외로 로그인 페이지로 돌아왔다 던지
        SavedRequest savedRequest = requestCache.getRequest(request,response);
        if (savedRequest != null){
            String targetUrl = savedRequest.getRedirectUrl(); // 이전에 가고자 했던 url로 가려면 redirectStrategy.sendRedirect에 넣어줌
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }else {
            // 기본 페이지로 보냄
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
