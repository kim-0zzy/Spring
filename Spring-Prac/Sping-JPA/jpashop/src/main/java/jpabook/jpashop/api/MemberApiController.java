package jpabook.jpashop.api;


import jpabook.jpashop.Controller.MemberForm;
import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
//@RestController 는 @Controller + @ResponseBody
@RestController //@ResponseBody은 데이터 자체를 바로 Json이나 xml로 보낼 때 사용함.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    // 이처럼 엔티티를 직접 반환하면 안된다. 왜냐면 엔티티 전체를 노출하기 때문에 원치 않은 정보 노출문제가 생길 수 있음.
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    //엔티티를 DTO로 변경하는 과정
    @Data @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }
    @Data @AllArgsConstructor
    static class MemberDto{
        private String name;
    }
    // 엔티티를 DTO로 변경하는 과정

    // 외부에서 오는 데이터에 엔티티를 1대1로 바인딩 받아서 사용하면 엔티티수정 시 api스펙이 망가져버린다.
    // 그러므로 엔티티를 외부 데이터와 격리시키고 DTO(v2의 방식)를 만들어서 사용할 것.
    @PostMapping("/api/v1/members") //          ↓ 이렇게하면 Json으로 넘어온 데이터를 Member로 바꿔줌.
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findMembers = memberService.findMembers();
        //.stream 대신에 루프를 돌려서 DTO객체를 하나하나 생성해도 됨.
        List<MemberDto> collect = findMembers.stream()
                .map(m-> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(),collect);

    }
    // 이렇게 DTO를 사용하면 장점
    // 1. 누군가가 엔티티를 수정하면 컴파일 오류를 내주어 변경사항을 인지하기 쉽다. (api스펙을 따라 바꾸거나 할 수 있음.)
    // 2. 안정적임. v1과 다르게 api의 스펙에 맞게 엔티티에서의 원하는 값만 받아올 수 있음. 원하는 값을 validation할 수도 있음.
    // 3. 유지보수가 상당히 효율적임.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);

    }

    // 회원수정 API
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberResponseV2(@PathVariable("id") Long id,
                                                       @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }
    @Data
    static class CreateMemberResponse{
        private Long id;
        public CreateMemberResponse(Long id){
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest{
        private String name;
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }
    @Data
    static class UpdateMemberRequest{
        private String name;
    }

}
