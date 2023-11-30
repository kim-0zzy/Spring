package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import java.util.List;
import java.util.Optional;
// interface란. 객체의 사용방법을 정의한 타입. 타형성을 구현하는 매우 중요한 역할.
// 클래스들이 구현해야하는 동작을 지정하는 용도로 사용되는 추상 자료형.
// 개발 코드를 수정하지 않고 사용하는 객체를 변경할 수 있도록 해준다.
// 확장에는 열려있고 결합도를 낮춘 유연한 방식의 프로그래밍이 가능해짐.
public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByName(String name);
    List<Member> findAll();
}
