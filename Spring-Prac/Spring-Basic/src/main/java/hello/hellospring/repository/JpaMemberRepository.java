package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository{

    private final EntityManager em; //JPA를 사용하려면 EntityManager를 주입받아야 한다.
    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override //이렇게만 하면 JPA가 insert 쿼리를 다 만들어서 넣어준다. 저장, 조회, 삭제하는건 JPA가 알아서 다 해줌, 코드짤필요 없음.
    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override // 근데 이거나 findAll같은 pk기반이 아닌 나머지 것들은 Jpql을 작성해 줘야함. 근데 스프링데이터JPA를 사용하면 이 두개도 안짜도됨.
    // 또 주의해야할 것이 JPA를 사용할 때 항상 트랜젝션이 있어야함. MemberService클래스로 ㄱㄱ #1
    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name", name)
                .getResultList();
        return result.stream().findAny();
    }

    @Override   //보통은 테이블을 대상으로 쿼리를 날리지만 아래 코드는 객체(엔티티)를 대상으로 쿼리를 날린다.
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }
}
