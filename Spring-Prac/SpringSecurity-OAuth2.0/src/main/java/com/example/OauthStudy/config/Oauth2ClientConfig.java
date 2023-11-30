package com.example.OauthStudy.config;


import com.example.OauthStudy.CustomAuthorityMapper;
import com.example.OauthStudy.service.CustomOAuth2UserService;
import com.example.OauthStudy.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class Oauth2ClientConfig {

    @Autowired
    private CustomOidcUserService customOidcUserService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().antMatchers(
                "/static/js/**", "/static/css/**", "/static/scss/**", "/static/images/**", "/static/icomoon/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeRequests(authRequest -> authRequest
                .antMatchers("/api/user").access("hasAnyRole('SCOPE_profile', 'SCOPE_email')")
                .antMatchers("/api/oidc").access("hasAnyRole('SCOPE_openid')")
                .antMatchers("/").permitAll()
                .anyRequest().authenticated());

        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig ->
                        userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService)));
        http.logout().logoutSuccessUrl("/");

        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper customAuthoritiesMapper(){
        return new CustomAuthorityMapper();
    }
}
