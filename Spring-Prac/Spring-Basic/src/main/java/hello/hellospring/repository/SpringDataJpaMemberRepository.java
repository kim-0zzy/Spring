package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// SpringDataJpaMemberRepository 가 JPaRepository를 상속받고 있으면 SpringDataJpaMemberRepository 가
// 구현체를 자동으로 만들어줌. 그리고 SpringDataJpa가 SpringBean에 자동으로 등록됨.
// 어떻게 JpaRepository만 사용해서 전체 기능을 사용할 수 있는가 ? 보면
// JpaRepository안에 웬만한 공통된 기능들이 다 탑재되어있음. 등록 삭제 조회 수정+@ 등등 그냥 다 가져다 쓰면 됨.
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository{
    // 아래것을 JPQL로 번역하면 -> select m from Member m where m.name = ?
    @Override
    Optional<Member> findByName(String name);
}
