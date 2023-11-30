package com.example.OauthStudy.converters;

public interface ProviderUserConverter<T,R> {

    R convert(T t);
}
