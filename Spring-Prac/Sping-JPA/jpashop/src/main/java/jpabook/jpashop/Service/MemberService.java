package jpabook.jpashop.Service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) //JPA의 모든 데이터 변경, 로직은 하나의 트랜젝션 안에서 실행되어야함. 그래서 이 어노테이션이 필요
// (readOnly = true)를 사용하면 조회메서드에서 성능향상 기대 가능. 쓰기메서드는 절대사용 안됨. 기본옵션 false
// 클래스 전체에 사용해도 되고 메서드마다 사용해도 되는데 여기선 전체사용함.
@RequiredArgsConstructor// 생성자 인젝션 코드를 줄여주는 기능.
public class MemberService {

    // set인젝션, 생성자인젝션, @AllArgsConstrucotr 인젝션, @Autowired + private 리포지토리;
    // 방식도 존재하나 각각 단점이 있으므로 @RequiredArgsConstructor 인젝션 사용할 것. 이게 가장 나음.(LOMBOK 기능)
    private final MemberRepository memberRepository;

    // 회원가입
    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    // 중복회원 검증 비즈니스 로직
    // member 엔티티의 name 인스턴스에 unique 제약조건을 걸어주는것이 중복회원가입에 안전하다.
    private void validateDuplicateMember(Member member){
        List<Member> findMembers =  memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    // 회원전체조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    // 회원단건조회
    public Member findOne(Long memberId){
         return memberRepository.findOne(memberId);
    }

    @Transactional // void타입 대신 Member타입으로 반환해도 된다. 하지만 그렇게 하면 준영속 상태의 Member객체가 반환된다.
                    // Commend와 Query의 분리를 위해 사용하지 않는 것이 좋다.
    public void update(Long id, String name){
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }


}
