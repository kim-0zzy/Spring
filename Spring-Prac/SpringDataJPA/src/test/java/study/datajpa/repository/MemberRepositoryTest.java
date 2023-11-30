package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성보장
    }

    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회 검사
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);
        //카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("BBB",20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA",10);
        Assertions.assertThat(result.get(0)).isEqualTo(member1);

    }
    /*
        MemberProjection, MemberRepository - findByMemberProjection 확인.
        네이티브 쿼리 사용법         네이티브 쿼리 사용 목적 : 대체로 DTO를 반환하고 싶을 때 사용
        한계가 뚜렷한 방법이다.
        데이터를 가져 오려면 select 절에 가져올 엔티티를 다 적어줘야하고, from 절에도 데이터를 다 찍어줘야 하고
        몇몇의 반환타입을 지원 안해준다. (Object[], Tuple, DTO 이 세 가지만 지원함)

        Sort 파라미터를 통한 정렬이 동작안할 가능성 있고 애플리케이션 로딩 시점에 문법 확인 불가능, 동적쿼리 사용 불가능.
        동적쿼리는 외부 라이브러리 사용하는 것을 추천한다. (Jdbc Template, myBatis, jooq)
        웬만하면 사용하지 않는게 좋다. 차라리 JdbcTemplate, myBatis 사용을 권장함.

    @Test
    public void nativeQuery(){
        //given
        Team teamA = new Team("TeamA");
        em.persist(teamA);

        Memeber m1 = new Member("m1",0,teamA);
        Memeber m2 = new Member("m2",0,teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        // ------------------ 네이티브 쿼리 -------------------
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result = " + result);

        // ------------------ 네이티브 쿼리의 프로젝션 활용 -------------------
        Page<MemberProjection> result = memberRepository.findByNativeProjection(pageRequest.of(0,10));
        for (MemberProjection memberProjection : content){
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());

    }

 */
}