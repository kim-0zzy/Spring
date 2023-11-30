package com.example.demo.security.configs;

import com.example.demo.security.filter.AjaxLoginProcessingFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class AjaxLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, AjaxLoginConfigurer<H>, AjaxLoginProcessingFilter> {

    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    private AuthenticationManager authenticationManager;

    // 생성자로 필터를 생성 -> 부모 클래스로 전달 = 부모 클래스로 부터 필터 참조 가능
    public AjaxLoginConfigurer() {
        super(new AjaxLoginProcessingFilter(), null);
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
    }

    @Override // H http -> http Security 객체가 넘어온 것
    public void configure(H http) {

        if(authenticationManager == null){ // ↓ 공유객체를 저장하고 가져올 수 있는 저장소 개념의 API
            authenticationManager = http.getSharedObject(AuthenticationManager.class);
        }
        // ↓ 부모 클래스에다 필터를 전달했기 때문에 getAuthenticationFilter 하면
        // AjaxLoginFilter 가져옴 AjaxSecurityConfig 클래스의 AjaxLoginProcessingFilter 메서드 부분의 set 부분을
        // 해당 파트에서 DSL로 진행하고 있는 것.
        getAuthenticationFilter().setAuthenticationManager(authenticationManager);
        getAuthenticationFilter().setAuthenticationSuccessHandler(successHandler);
        getAuthenticationFilter().setAuthenticationFailureHandler(failureHandler);

        // 세선셜정
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            getAuthenticationFilter().setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }

        // 리멤버미 설정
        RememberMeServices rememberMeServices = http
                .getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            getAuthenticationFilter().setRememberMeServices(rememberMeServices);
        }
        // 공유객체에 직접 만든 필터 저장
        http.setSharedObject(AjaxLoginProcessingFilter.class,getAuthenticationFilter());
        // AjaxSecurityConfig 클래스의 configure(HttpSecurity http) 부분의 addFilterBefore 를 여기에서 처리
        http.addFilterBefore(getAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // ↓↓↓
    // 여기는 직접 만든 핸들러를 주입해서 return 해주는 메서드
    public AjaxLoginConfigurer<H> successHandlerAjax(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public AjaxLoginConfigurer<H> failureHandlerAjax(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }

    public AjaxLoginConfigurer<H> setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        return this;
    }
    /// ↑↑↑

    // ↓↓↓ RequestMatcher 에게 LoginProcessUrl를 파라미터로 전달해 셋팅하는 메서드
    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
