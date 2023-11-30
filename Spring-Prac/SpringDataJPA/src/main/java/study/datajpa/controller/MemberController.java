package study.datajpa.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    // 일반적인 방법.
    @GetMapping("/members{id}")
    public String findMember(@PathVariable("id") Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    // S-DataJPA의 Web확장-도메인 클래스 컨버터 방법.
    // HTTP 요청은 회원ID를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 반환.
    // 도메인 클래스 컨버터도 리포지토리를 사용해서 엔티티를 찾음.
    // Q. 이렇게 하면 엔티티 노출되지 않나?
    // A. 맞음. 그냥 단순 조회용으로만 사용할 것.
    @GetMapping("/members2{id}")
    public String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")   // ↓ 개별 수정하려면 이런식으로 @PageableDefault 어노테이션 사용할 것.
    public Page<MemberDto> list(/*@PageableDefault(size = 5, sort = "username")*/ Pageable pageable){

        /*Page<Member> page = memberRepository.findAll(pageable);
        return page;*/ // 1. 엔티티 그대로 반환 시 사용법 (이거 사용 시 메서드 반환타입을 Member로 바꿔야함.)

        /*return memberRepository.findAll(pageable)       // 2. DTO로 반환 시 사용법
                .map(m -> new MemberDto(m.getId(), m.getUsername(),null));*/

        return memberRepository.findAll(pageable).map(MemberDto::new); // 3. 엔티티를 받은 DTO를 반환하는 법.
        // 1->3으로 갈 수록 코드가 간결해짐.

        // S-DataJpa의 기본 쿼리(Not custom) 중 어떤 쿼리라도 pageable을 파라미터로 넘기면 페이징 처리를 할 수 있음.
        // but, 요청 파라미터에 한정함. ex) /members?page=0&size=3&sort=id,desc&sort=username,desc
        // 기본값은 0페이지부터 / 한페이지 20개 / ASC | 글로벌 설정을 수정하려면 .yml 수정할 것.       ※

        // 해당 API를 날려보면 totalPages, totalElements, size 등등 여러가지 데이터가 반환되는 것이 많은데
        // 이게 반환되는 이유는 해당 메서드의 반환타입이 Page 이기 때문에 자동으로 반환되는 것.

        // @Qualifier(" 접두사명 ")도 있는데 이거는 강의자료 찾아볼 것. P. 54
        // Page를 1부터 시작하는 방법도 있는데 별로 권장하지 않음. P. 55
    }

    // 테스트용 데이터 생성 메서드
    @PostConstruct
    public void init(){
        memberRepository.save(new Member("userA"));
    }
}
