package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;

// Interface 기반의 projection
// MemberRepository - findProjectionsByUsername 참조.
public interface UsernameOnly {
    @Value("#{target.username + ' ' + target.age}")
    // @Value 를 사용하면 username과 age를 문자열을 더해서 가져와준다.     [target.] -> MemberRepository에서 쓰면 Member엔티티.
    // @Value 를 사용하면 Open Projection 이라고 함. 사용 안하면 Close Projection
    String getUsername();

}

/* -> 작성 후 MemberRepository의 findProjectionsByUsername 으로
    //given
    @Test
    public void projections(){
        Team teamA = new Team("TeamA");
        em.persist(teamA);

        Memeber m1 = new Member("m1",0,teamA);
        Memeber m2 = new Member("m2",0,teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<UsernameOnly< result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnly usernameOnly : result){
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
    }
*/

// 원하는 데이터만 가지고 오려 할 때 도움이 되는 인터페이스 기술.
//
