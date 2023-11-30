package jpabook.jpashop.Controller;


import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new") //@Valid를 사용하면 해당 클래스에 있는 validation 기능을 사용할 수 있다. (@NotEmpty)
                                // BindingResult를 사용하면 해당 메서드에서 발생한 오류를 사용할 수 있음.
                                // 단순한 Application이면 엔티티를 바인딩 받아 사용해도 되지만 실무에선 단순할 수가 없음.
    public String create(@Valid MemberForm Form, BindingResult result){

        if(result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address =  new Address(Form.getCity(), Form.getStreet(), Form.getZipcode());

        Member member = new Member();
        member.setName(Form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "members/memberList";
    }


}
