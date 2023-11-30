package com.example.OauthStudy.model.users;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import java.util.List;

@Data
@Builder
// JPA 사용 시 해당 클래스는 Entity
public class User {
    private String registrationId;
    private String id;
    private String username;
    private String password;
    private String provider;
    private String email;
    private List<? extends GrantedAuthority> authorities;


}
