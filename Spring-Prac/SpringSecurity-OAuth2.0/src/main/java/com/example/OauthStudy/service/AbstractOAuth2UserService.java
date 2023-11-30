package com.example.OauthStudy.service;

import com.example.OauthStudy.converters.ProviderUserConverter;
import com.example.OauthStudy.converters.ProviderUserRequest;
import com.example.OauthStudy.model.*;
import com.example.OauthStudy.model.social.GoogleUser;
import com.example.OauthStudy.model.social.KeycloakUser;
import com.example.OauthStudy.model.social.NaverUser;
import com.example.OauthStudy.model.users.User;
import com.example.OauthStudy.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Getter
public abstract class AbstractOAuth2UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProviderUserConverter<ProviderUserRequest, ProviderUser> providerUserConverter;

    public void register(ProviderUser providerUser, OAuth2UserRequest userRequest) {
        User user = userRepository.findByUsername(providerUser.getUsername());

        if(user == null){
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            userService.register(registrationId, providerUser);
        }else{
            System.out.println("user = " + user);
        }
    }

//    public ProviderUser providerUser(ClientRegistration clientRegistration, OAuth2User oAuth2User)
    public ProviderUser providerUser(ProviderUserRequest providerUserRequest ){

//        String registrationId = clientRegistration.getRegistrationId();
//        if(registrationId.equals("keycloak")){
//            return new KeycloakUser(oAuth2User, clientRegistration);
//        }else if (registrationId.equals("google")){
//            return new GoogleUser(oAuth2User, clientRegistration);
//        }else if (registrationId.equals("naver")){
//            return new NaverUser(oAuth2User, clientRegistration);
//        }
        return providerUserConverter.convert(providerUserRequest);

    }


}
