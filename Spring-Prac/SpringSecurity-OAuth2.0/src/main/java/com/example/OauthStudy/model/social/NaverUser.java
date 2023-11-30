package com.example.OauthStudy.model.social;

import com.example.OauthStudy.model.Attributes;
import com.example.OauthStudy.model.Oauth2ProviderUser;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class NaverUser extends Oauth2ProviderUser {


    public NaverUser(Attributes attributes, OAuth2User oAuth2User, ClientRegistration clientRegistration) {
//        super((Map<String, Object>)oAuth2User.getAttributes().get("response"), oAuth2User, clientRegistration);
        super(attributes.getSubAttributes(), oAuth2User, clientRegistration);
    }

    @Override
    public String getId() { // 식별자 id
        return (String) getAttributes().get("id");
    }

    @Override
    public String getUsername() { // 실제 id (kimdudtj3070)
        return (String) getAttributes().get("name");
    }

}
