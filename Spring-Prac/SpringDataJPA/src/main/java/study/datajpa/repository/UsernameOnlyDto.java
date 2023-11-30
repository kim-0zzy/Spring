package study.datajpa.repository;

// 클래스 기반의 projection
// MemberRepository - findProjectionsByUsername 참조.
public class UsernameOnlyDto {

    private final String username;

    public String getUsername() {
        return username;
    }

    // 클래스 기반에서는 생성자의 파라미터 이름을 기반으로 분석해 쿼리를 생성한다.
    public UsernameOnlyDto(String username) {
        this.username = username;
    }
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
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnlyDto usernameOnly : result){
            System.out.println("usernameOnly = " + usernameOnly.getUsername());
    }
*/
