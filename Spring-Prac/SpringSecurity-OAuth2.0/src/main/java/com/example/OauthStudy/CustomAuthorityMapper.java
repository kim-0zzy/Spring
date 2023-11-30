package com.example.OauthStudy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;
import java.util.HashSet;

public class CustomAuthorityMapper implements GrantedAuthoritiesMapper {
    private String prefix = "ROLE_";
//    HashSet<GrantedAuthority>
    @Override
    public Collection<? extends GrantedAuthority>  mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        HashSet<GrantedAuthority> mapped = new HashSet<>(authorities.size());
        for (GrantedAuthority authority : authorities) {
            mapped.add(mapAuthority(authority.getAuthority()));
        }

        return mapped;
    }

    private GrantedAuthority mapAuthority(String name) {
        if(name.lastIndexOf(".") > 0){
            int index = name.lastIndexOf("."); // -> ex) https://google.com/asd/asd/asd.email 에서 .을 기준으로 슬라이싱
            name = "SCOPE_" + name.substring(index+1); // -> .을 기준으로 좌측은 -1 우측은 +1이니까 +1해서 가져옴
        }
        if (this.prefix.length() > 0 && !name.startsWith(this.prefix)) {
            name = this.prefix + name;
        }
        return new SimpleGrantedAuthority(name);
    }
}
