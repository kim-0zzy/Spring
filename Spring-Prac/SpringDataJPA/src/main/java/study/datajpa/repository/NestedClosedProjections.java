package study.datajpa.repository;

public interface NestedClosedProjections {


    // 이 기능을 사용 시 첫번째 필드까지는 최적화 해서 필드명을 기반으로 원하는 정보만 가져올 수 있지만
    // 두번째 필드부터는 최적화가 불가능해 엔티티 전체를 불러온다. (성능 저하 요인)
    // caution !
    // 프로젝션 대상이 Root 엔티티면 JPQL Select 절 최적화 가능. (첫 번째 필드)
    // 아니라면 Left Outer Join 처리, 모든 필드를 Select 해서 엔티티로 조회한 후 계산 (두 번째 필드)
    // 프로젝션 대상이 root 엔티티면 유용, 아니면 최적화 불가능, 실무에 아직은 부적합함, QueryDsl 사용 권장.
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }

}

/*
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
        List<NestedClosedProjections< result = memberRepository.findProjectionsByUsername("m1");

        for (NestedClosedProjections nestedClosedProjections : result){
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);

            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName);
        }
*/