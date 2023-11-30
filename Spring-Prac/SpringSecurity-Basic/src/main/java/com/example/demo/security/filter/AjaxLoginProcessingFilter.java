package com.example.demo.security.filter;

import com.example.demo.entity.MemberDTO;
import com.example.demo.security.token.AjaxAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper = new ObjectMapper();


    public AjaxLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/api/login"));
        // 사용자가 해당 URL로 요청을 했을 때 정보가 매칭되면 필터가 작동되는 방식
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if(! isAjax(request)){
            throw new IllegalStateException("Authentication is not supported");
        }
        MemberDTO memberDTO = objectMapper.readValue(request.getReader(), MemberDTO.class);
        if(StringUtils.isEmpty(memberDTO.getUsername()) || StringUtils.isEmpty(memberDTO.getPassword())){
            throw new IllegalArgumentException("Username or Password is empty");
        }

        AjaxAuthenticationToken authenticationToken = new AjaxAuthenticationToken(memberDTO.getUsername(), memberDTO.getPassword());

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    // 사용자가 헤더에 어떤 정보를 담아 요청을 보낼 때
    // 아래 if문에 해당되는 조건이면 AJAX 요청임을 확인하는 메서드
    private boolean isAjax(HttpServletRequest request){
        if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))){
            return true;
        }
        return false;
    }
}
