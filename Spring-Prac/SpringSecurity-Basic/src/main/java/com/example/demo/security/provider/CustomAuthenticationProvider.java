package com.example.demo.security.provider;

import com.example.demo.security.MemberContext;
import com.example.demo.security.common.FormWebAuthenticationDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String)authentication.getCredentials();

        MemberContext memberContext = (MemberContext)userDetailsService.loadUserByUsername(username);
        if(!passwordEncoder.matches(password, memberContext.getMember().getPassword())){
            throw new BadCredentialsException("BadCredentialsException");
        }
        // 로그인 시도 시 시크릿 키 검증 테스트 ( 인증부가기능 3-9 ) ( 캡스톤에선 빼고 쓸 것 ) 이거 넣으면 로그인 폼에서 데이터 안넘어가는 버그 있음
        // Common package
//        FormWebAuthenticationDetails formWebAuthenticationDetails = (FormWebAuthenticationDetails) authentication.getDetails();
//        String secretKey = formWebAuthenticationDetails.getSecretKey();
//        if(secretKey == null || "secret".equals(secretKey)){
//            throw new InsufficientAuthenticationException("InsufficientAuthenticationException");
//        }
        // 로그인 시도 시 시크릿 키 검증 테스트

        // 3-5 9분대 UsernamePasswordAuthenticationToken 의 2가지 생성자 역할 확인
        return new UsernamePasswordAuthenticationToken(memberContext.getMember(), null, memberContext.getAuthorities());
    }

    @Override// 3-5 3분대 다시 듣기 (역할이 뭔지)
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
