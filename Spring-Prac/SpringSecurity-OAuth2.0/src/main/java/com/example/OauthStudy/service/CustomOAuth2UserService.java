package com.example.OauthStudy.service;

import com.example.OauthStudy.converters.ProviderUserRequest;
import com.example.OauthStudy.model.ProviderUser;
import lombok.Getter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Getter
public class CustomOAuth2UserService extends AbstractOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2userService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2userService.loadUser(userRequest);

        ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User, null);

//        ProviderUser providerUser = super.providerUser(clientRegistration, oAuth2User);
        ProviderUser providerUser = providerUser(providerUserRequest);

        //회원가입
        super.register(providerUser, userRequest);

        return oAuth2User;
    }
}
