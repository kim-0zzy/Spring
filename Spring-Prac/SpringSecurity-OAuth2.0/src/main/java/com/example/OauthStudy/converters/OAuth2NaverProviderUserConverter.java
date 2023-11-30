package com.example.OauthStudy.converters;

import com.example.OauthStudy.enums.OAuth2Config;
import com.example.OauthStudy.model.ProviderUser;
import com.example.OauthStudy.model.social.NaverUser;
import com.example.OauthStudy.util.OAuth2Utils;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2NaverProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser>{
    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if(providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.NAVER.getSocialName())){
            return null;
        }

        return new NaverUser(OAuth2Utils.getSubAttributes(providerUserRequest.oAuth2User(), "response"),
                providerUserRequest.oAuth2User(),
                providerUserRequest.clientRegistration());
    }
}
