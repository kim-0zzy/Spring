package com.example.OauthStudy.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// 그냥 LoginController에 있는 권한 부여 요청을 Filter 형식으로 변경한 클래스임.
// 8-9
public class CustomOAuth2AuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    public static final String DEFAULT_FILTER_PROCESSING_URI = "/oauth2Login/**";
    // 원래는 상수로 설정하는게 아닌데 예시를 위해서 이렇게 설정함.

    private DefaultOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    private OAuth2AuthorizationSuccessHandler successHandler;
    private Duration clockSkew = Duration.ofSeconds(3600);
    private Clock clock = Clock.systemUTC();


    public CustomOAuth2AuthenticationFilter(
            DefaultOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager,
            OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {

        super(DEFAULT_FILTER_PROCESSING_URI);
        // 웹에서 요청한 URL과 해당 클래스 메서드(부모 클래스)에서 설정한 URL이 매칭되면 해당 필터가 실행되는 구성
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
        this.oAuth2AuthorizedClientRepository = oAuth2AuthorizedClientRepository;
        this.successHandler = (authorizedClient, principal, attributes) -> {
            // 해당 핸들러가 작동될 첫 시점에 principal에 저장된 것은 익명사용자이기에 사용자 정보를 가져와 인증처리를 하지 않음.
            // 그래서 해당 attemptAuthentication 메서드의 최하단에서 해당 핸들러를 다시 한번 호출해 인증처리 된 사용자를 다시 한번 저장함.
            oAuth2AuthorizedClientRepository
                    .saveAuthorizedClient(authorizedClient, principal,
                            (HttpServletRequest) attributes.get(HttpServletRequest.class.getName()),
                            (HttpServletResponse) attributes.get(HttpServletResponse.class.getName()));
            System.out.println("authorizedClient = " + authorizedClient);
            System.out.println("principal = " + principal);
            System.out.println("attributes = " + attributes);
        };

        oAuth2AuthorizedClientManager.setAuthorizationSuccessHandler(successHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Controller(SpringMVC 활용 단계) 에서는 해당 구문이 Null이 될 수 없음.
        // 왜냐 Controller를 사용할 땐 인증받지 못한 사용자라도 익명사용자로 저장이 되어 넘어오게 설계되어있음.
        // 근데 필터 기반에서는 null이 가능함. Oauth2ClientConfig 클래스의 addFilterBefore APi를 보면
        // 설계한 필터가 UsernamePasswordAuthenticationFilter 앞으로 위치하게 만들었는데
        // 익명사용자를 만드는 필터는 UsernamePasswordAuthenticationFilter 필터 뒤에 위치함.
        if(authentication == null){
            authentication =
                    new AnonymousAuthenticationToken("anonymous","anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
        }

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal(authentication)
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();


        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient != null &&
                hasTokenExpired(authorizedClient.getAccessToken()) &&
                authorizedClient.getRefreshToken() != null) {
                authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizeRequest);
            }

            if (authorizedClient != null &&
                    hasTokenExpired(authorizedClient.getAccessToken()) &&
                    authorizedClient.getRefreshToken() != null) {
                oAuth2AuthorizedClientManager.authorize(authorizeRequest);
            }

            if (authorizedClient != null) {
                OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
                ClientRegistration clientRegistration = authorizedClient.getClientRegistration();
                OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, accessToken);
                OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);


                SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
                authorityMapper.setPrefix("SYSTEM_");
                Set<GrantedAuthority> grantedAuthorities = authorityMapper.mapAuthorities(oAuth2User.getAuthorities());

                OAuth2AuthenticationToken oAuth2AuthenticationToken =
                        new OAuth2AuthenticationToken(oAuth2User, grantedAuthorities, clientRegistration.getRegistrationId());

                SecurityContextHolder.getContext().setAuthentication(oAuth2AuthenticationToken);

                //
                this.successHandler.onAuthorizationSuccess(authorizedClient, oAuth2AuthenticationToken,
                        createAttributes(request, response));

                return oAuth2AuthenticationToken;
            }

        return null;
    }

    private boolean hasTokenExpired(OAuth2Token token){
        return this.clock.instant().isAfter(token.getExpiresAt().minus(this.clockSkew));
    }

    private static Map<String, Object> createAttributes(HttpServletRequest servletRequest,
                                                        HttpServletResponse servletResponse){

        Map<String, Object> attributes = new HashMap<>();
        attributes.put(HttpServletRequest.class.getName(), servletRequest);
        attributes.put(HttpServletResponse.class.getName(), servletResponse);
        return attributes;
    }
}
// 8-9