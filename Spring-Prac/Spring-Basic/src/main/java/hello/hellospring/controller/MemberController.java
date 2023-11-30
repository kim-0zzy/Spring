package hello.hellospring.controller;


import hello.hellospring.domain.Member;
import hello.hellospring.service.memberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // 컨트롤러는 스프링이 관리하는것 이기 때문에 얘는 어쩔수 없이 어노테이션 사용해야함. 바로 밑 AutoWired도 마찬가지
public class MemberController {

    private final memberService memberService;

    @Autowired  //생성자에 해당 어노테이션을 사용하면 스프링이 컨테이너에 있는 멤버서비스를 연결시켜줌
    // 이것은 DI의 생성자 주입방식이라고 함. 이 외에 필드주입(별로안좋음, 변경이 불가능함),
    // setter 주입(setter가 public으로 노출이 되어 안좋음)이 있음 → 아무 프로그래머나 호출할 수 있기때문에 안좋음, 바꿔치기 가능성↑
    public MemberController(memberService memberService) {
        this.memberService = memberService;
    }
    @GetMapping("/members/new")
    public String createForm(){
        return "members/createMemberForm";
    }

    /* GetMapping은 조회할때 사용하고 (url창에 치는거) PostMapping은 Form에 넣어서 전달할때 사용 (데이터를 등록할 때).*/
    @PostMapping("/members/new")
    public String create(MemberForm form){
        Member member = new Member();
        member.setName(form.getName());
        memberService.join(member);
        return "redirect:/"; // redirect:/홈 화면으로 보내버리는 기능
    }
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
