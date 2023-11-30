package com.example.demo.security.configs;

import com.example.demo.security.Handler.CustomAuthenticationAccessDinedHandler;
import com.example.demo.security.factory.UrlResourcesMapFactoryBean;
import com.example.demo.security.metadatasource.UrlSecurityMetadataSource;
import com.example.demo.security.provider.CustomAuthenticationProvider;
import com.example.demo.security.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;
//    @Autowired
//    private AccessDeniedHandler customAuthenticationAccessDeniedHandler;
    @Autowired
    private AuthenticationDetailsSource authenticationDetailsSource;
    @Autowired // 5-5 추가 21:02
    private SecurityResourceService securityResourceService;


    @Override
    public void configure(WebSecurity web) throws Exception {
        // 보안필터 바깥에서 적용됨. 보안 필터에 진입조차 안함
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Autowired
//    private UserDetailsService userDetailsService;
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService);
//    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        return new CustomAuthenticationProvider();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    // 인가 예외는  exception translate filter가 위임받아 처리해야함.
    // 그러기에 Failure Handler가 아닌 AccessDeniedHandler가 처리해야함.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/","/users","user/login/**","/login**").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
        .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
//                .and()
//                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(authenticationDetailsSource)
                .defaultSuccessUrl("/")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()

        ;

    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        CustomAuthenticationAccessDinedHandler accessDeniedHandler = new CustomAuthenticationAccessDinedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

    // ↓↓↓↓ 직접만든 UrlFilterInvocationSecurityMetadataSource로 인가처리를 하기 위해 하는 작업
    @Bean
    public FilterSecurityInterceptor customFilterSecurityInterceptor() throws Exception {

        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();

        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());

        return filterSecurityInterceptor;
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlSecurityMetadataSource(/*5-5 추가*/urlResourcesMapFactoryBean().getObject(), securityResourceService);
    }

    private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {
        UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
        urlResourcesMapFactoryBean.setSecurityResourcesService(securityResourceService);
        return urlResourcesMapFactoryBean;
    }

    private AccessDecisionManager affirmativeBased(){
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoter());
        return affirmativeBased;
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoter() {
        return Arrays.asList(new RoleVoter());
    }

    // ↑↑↑↑ 직접만든 UrlFilterInvocationSecurityMetadataSource로 인가처리를 하기 위해 하는 작업
    // 이 후 .and()
    //                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
    //를 추가해 인가처리에 추가할 수 있음.
}
