package com.example.OauthStudy.converters;

import com.example.OauthStudy.enums.OAuth2Config;
import com.example.OauthStudy.model.ProviderUser;
import com.example.OauthStudy.model.social.GoogleUser;
import com.example.OauthStudy.util.OAuth2Utils;

public class OAuth2GoogleProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser>{


    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {

        if(providerUserRequest.clientRegistration().getRegistrationId().equals(OAuth2Config.SocialType.GOOGLE.getSocialName())){
            return null;
        }

        return new GoogleUser(OAuth2Utils.getMainAttributes(providerUserRequest.oAuth2User())
                ,providerUserRequest.oAuth2User()
                ,providerUserRequest.clientRegistration());
    }
}
