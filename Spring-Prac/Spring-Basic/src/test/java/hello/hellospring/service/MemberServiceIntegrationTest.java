package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional //아래 beforeEach와 AfterEach를 자동으로 실행해주는 기능.
// 트랜잭션을 먼저 실행 한 후 db에 데이터를 insertQurey한 후 테스트가 끝나면 Rollback 해줌. (지운다 x, 반영을 안한다.)
// @Commit 어노테이션을 사용하면 저장할 수도 있음.
// ★ 가급적 단위테스트가 훨씬 좋은 테스트일 확률이 높음, 스프링컨테이너가 없어도 테스트 할 수 있도록 훈련해야함. 통합테스트 자제.
public class MemberServiceIntegrationTest {

    // dependencyInjection이라고 함. 이렇게 쓰는 이유?
    // 같은 객체의 db로 테스트 해야하기 때문에.
    // 다른 객체로 테스트 하면 꼬일 위험이 있음.
    @Autowired
    memberService memberService;
    @Autowired
    MemberRepository memberRepository;

/*  테스트하며 db에 반영된것을 실행전, 실행후 삭제해주는 기능.
    @BeforeEach
    public void beforEach(){
        memberRepository = new MemoryMemberRepository();
        memberService = new memberService(memberRepository);
    }

    @AfterEach
    public void afterEach(){
        memberRepository.clearStore();
    }
*/
    @Test
    void join() {
        //given 무언가 주어졌을때
        Member member = new Member();
        member.setName("hello");
        //when  이걸 실행했을대
        Long saveId = memberService.join(member);
        //then  이 결과가 나와야함.   ★
        Member findmember = memberService.findOne(saveId).get();
        Assertions.assertThat(member.getName()).isEqualTo(findmember.getName());
    }
    @Test
    public void 중복_회원_예외(){
        //given
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");
        //when

        /* 이렇게 트라이-캐치를 써도 되지만
        memberService.join(member1);
        try{
            memberService.join(member2);
            fail();
        }catch (IllegalStateException e){
            Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
        */
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");

        //then
    }

    @Test
    void findMembers() {
    }

    @Test
    void findOne() {
    }
}
