package com.example.demo.controller;

import com.example.demo.entity.Member;
import com.example.demo.entity.MemberDTO;
import com.example.demo.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/myPage")
    public String myPage() throws Exception{
        return "/user/mypage";
    }

    @GetMapping("/users")
    public String createMember(){
        return "user/login/register";
    }

    @PostMapping("/users")
    public String createMember(MemberDTO memberDTO){

        Member member = new Member(memberDTO.getUsername(), passwordEncoder.encode(memberDTO.getPassword())
        ,memberDTO.getEmail(), memberDTO.getAge(), memberDTO.getRole());
        memberService.createMember(member);

        return "redirect:/";
    }
}
