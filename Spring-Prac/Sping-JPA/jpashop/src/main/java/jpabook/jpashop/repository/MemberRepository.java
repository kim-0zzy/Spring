package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    //MemberService의 인젝션 주석 읽기, 원래는 @PersistenceContext를 사용해야만 하지만 스프링이 이걸 지원해줌.
    private final EntityManager em;

    /* emf를 직접 주입받고싶을때 이렇게 사용. 근데 거의 안씀
    @PersistenceUnit
    private EntityManagerFactory emf;
    */
    public void save(Member member){
        em.persist(member);
        // return member.getId(); 커맨드와 쿼리를 분리하기 위해 엔티티를 반환하지 않고 id정도만 반환함.
        // Side Effect 때문에 웬만하면 return 값을 만들지 않음.
    } // save메소드로 인해 Insert쿼리가 날라감.

    public Member findOne(Long id){
        return em.find(Member.class, id);
    } // 단건조회, (type, Pk)

    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    } // 전체조회

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }// 파라미터 바인딩
}
