package com.example.OauthStudy.converters;

import com.example.OauthStudy.model.ProviderUser;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


@Component
public class DelegatingProviderUserConverter implements ProviderUserConverter<ProviderUserRequest, ProviderUser> {
    private List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> converters;

    public DelegatingProviderUserConverter() {
        List<ProviderUserConverter<ProviderUserRequest, ProviderUser>> providerUserConverters =
                Arrays.asList(new OAuth2GoogleProviderUserConverter(), new OAuth2NaverProviderUserConverter(), new OAuth2KakaoProviderUserConverter());

        this.converters = Collections.unmodifiableList(new LinkedList<>(providerUserConverters));
    }

    @Override
    public ProviderUser convert(ProviderUserRequest providerUserRequest) {
        Assert.notNull(providerUserRequest, "providerUserRequest cannot be null");

        for(ProviderUserConverter<ProviderUserRequest, ProviderUser> converter : this.converters){
            ProviderUser providerUser = converter.convert(providerUserRequest);
            if(providerUser != null) return providerUser;

        }
        return null;
    }
}
