package com.example.demo.security.metadatasource;

import com.example.demo.security.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
public class UrlSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    // LinkedHashMap<RequestMatcher, List<ConfigAttribute>> 를 통해서 RequestMap 구현.
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap = new LinkedHashMap<>();
    private SecurityResourceService securityResourceService;

    // 5-6 securityResourceService 추가
    public UrlSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourcesMap, SecurityResourceService securityResourceService) {
        this.requestMap = resourcesMap;
        this.securityResourceService = securityResourceService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // 파라미터로 전달된 object를 FilterInvocation으로 type cast한 후 HttpServletRequest 객체를 생성해서 .getRequest()로
        // 현재 사용자가 요청하는 요청객체를 얻을 수 있다.
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        // 5-4 23:00 현재 아무런 권한정보가 저장되어있지 않기 때문에 for문을 돌아도 null을 반환한다.
        // null을 반환핬기 때문에 권한이 없어도 인가처리가 된다.
        // 이 후 .and() 를 추가함으로 직접만든 필터가 앞으로 위치한다.
        //                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        // 또 직접만든 필터가 실행된 후 기존의 필터가 실행되는데 이 때 페이지별로 직접 인가권한을 설정한 api들이 남아있긴 해도
        // 중복으로 인가 권한 테스트를 하지 않는다.
        requestMap.put(new AntPathRequestMatcher("/mypage"), Arrays.asList(new SecurityConfig("ROLE_USER")));
        // ↑ 이 방법으로 권한정보를 추가할 수 있다. 그러나 이건 테스트용 임시 방법. 추 후 5-5에서 DB에 추가하는 방식을 구현할 것. (5-4, 27:10)
        if(requestMap != null){
            for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()){
                RequestMatcher matcher = entry.getKey();
                if(matcher.matches(request)){
                    return entry.getValue(); // .getValue()는 권한정보. 권한정보 반환
                }
            }
        }
        return null;
    }
    // SecurityConfig 클래스의 FilterSecurityInterceptor 메서드로 ㄱㄱ

    // 당장쓰진 않지만 일단 복붙해놓은 것. 5-4(13:09)
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()){
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }
    // 당장쓰진 않지만 일단 복붙해놓은 것. 5-4(13:09)
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    // 접근권한 업데이트 시 반영시키는 로직
    public void reload(){
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = securityResourceService.getResourceList();
        Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadedMap.entrySet().iterator();

        requestMap.clear();

        while (iterator.hasNext()){
            Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = iterator.next();
            requestMap.put(entry.getKey(),entry.getValue());
        }
    }
}
