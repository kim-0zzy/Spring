package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
// 리포지토리 인터페이스는 단순히 그 기능에 관련한 용어지만
// 서비스클래스에는 비즈니스에 가까운 용어를 사용해야함.
// 그래야 개발자들이 기능 찾는데 딱 매핑이됨.


// @Service
@Transactional //#1 JpaMemberRepository 클래스 ㄱㄱ
public class memberService {

    private final MemberRepository memberRepository; // = new MemoryMemberRepository();

    // DI를 하기 위한 생성자.
   // @Autowired //
    public memberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //회원가입
    // #2 JPA는 join 들어올때 모든 데이터 변경이 항상 트랜젝션 안에서 실행되어야한다.
        public long join(Member member){
            //동명 중복회원 X
            validateDupliateMember(member); //중복회원 검증 메소드
            memberRepository.save(member);
            return member.getId();
    }

    //원래 validateDupliateMember메서드는 join에 들어가는 기능이지만,
    // 어떠한 로직이 나올 경우 다른 메서드로 분리시키는게 좋다. ex).findByName
    private void validateDupliateMember(Member member){
                    /* Optional<Member> result =  memberRepository.findByName(member.getName());
            result.ifPresent(m -> {  //ifPresent가 뭐냐면 Null이 아닌 값이 있을경우 동작하는 로직.
                throw new IllegalStateException("이미 존재하는 회원입니다.");
                //get()으로 꺼내도 되지만 직접 꺼내는걸 권장하지 않음. */
        memberRepository.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                }); //이렇게 사용하는 것이 편함. 어차피 Optional객체를 만든 후 반환하는 것이기 때문에 똑같음.
    }

    //전체회원 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Optional<Member> findOne(Long memberId){
            return  memberRepository.findById(memberId);
    }
}
