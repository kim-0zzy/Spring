package com.example.demo.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
public class MemberDTO {

    private String username;
    private String password;
    private String email;
    private String age;
    private String role;
}
