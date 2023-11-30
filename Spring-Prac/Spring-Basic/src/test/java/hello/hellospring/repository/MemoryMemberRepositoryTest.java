package hello.hellospring.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import hello.hellospring.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

//Q. 수백개의 기능이 있을때는 어떻게 해야하나요?
//A. test폴더의 hello.hellospring 우클릭 후 Run 'Test in hello.hellospring' 누르면 알아서 다 해줌.

class MemoryMemberRepositoryTest {
    MemoryMemberRepository repository = new MemoryMemberRepository();

    // 테스트할때 메소드는 순서에 상관없이 막 돌아가기때문에 한 메소드가 끝났을때 메모리를 비워줘야함.
    // 그때 사용하기 위해 이 메소드를 추가. Test클래스 말고 본문에도 추가해줘야함
    // 테스트는 반드시 메소드별로 의존관계가 없이 설계되어야함.
    @AfterEach
    public void afterEach(){
        repository.clearStore();
    }


    @Test
    public void save(){
        Member member = new Member();
        member.setName("spring");

        repository.save(member);

        Member result = repository.findById(member.getId()).get();
        // System.out.println("result = " + (result == member)); 이렇게 출력해서 봐도 되지만 매번 실행해서 보기 힘들기때문에
        //Assertions.assertEquals(member,result);  // 이렇게 많이 사용함. 그런데
        Assertions.assertThat(member).isEqualTo(result); // 요즘은 이걸 더 많이 사용함.
    }

    @Test
    public void findByName(){
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

         Member result = repository.findByName("spring1").get();
         Assertions.assertThat(result).isEqualTo(member1);
    }

    @Test
    public void findAll(){
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        List<Member> result = repository.findAll();
        Assertions.assertThat(result.size()).isEqualTo(2);
    }
}
