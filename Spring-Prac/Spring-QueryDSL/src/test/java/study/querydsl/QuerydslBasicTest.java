package study.querydsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;
    // 이렇게 필드로 빼놓으면 멀티스레드에서의 접근 시 동시성 문제가 터질 수 있지 않을까? 라고 생각하지만
    // JPA에서 트랜젝션별로 자등으로 분리해줘 문제없다.

    @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",20,teamA);

        Member member3 = new Member("member3",30,teamB);
        Member member4 = new Member("member4",40,teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

    }

    @Test
    public void startJPQL(){
        // find member1
        String qlString = "select m from Member m where m.username = :username";

        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username","member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");

    }
    @Test
    public void startQuerydsl(){
        // JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        // ↑ 이렇게 JPAQueryFactory 를 생성해서 사용해도 되지만 필드로 빼줄 수 있다.
/*
        QMember m = new QMember("m"); <-  생성자에 "m"는 어떤 QMember 인지 구분하는 값. (이거 말고 다른거 씀 추 후에 소개)
                                          ex) 같은 테이블을 조인해야 할 경우 서로를 구분하기 위해 사용
        QMember m = QMember.member;  <- 이렇게 사용하는것도 좋다. 아니면 Static Import 를 사용하는 방법도 좋음.
*/
        Member findMember = queryFactory // Static import 예시. 일단 QMember.member 집어넣고 Alt+Enter -> Static import 선택
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) // 파라미터 바인딩을 따로 안해줘도 JPA에서 자동으로 바인딩 해준다.
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                // 이렇게 .and로 끊어가도 되지만 searchAndParam 처럼 ,로 파라미터를 추가로 넘겨도 and 처리 해준다.
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam(){
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),member.age.eq(10))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultFetch(){
        /*
        //리스트 조회 [데이터가 없으면 빈 리스트 반환]
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        //단건 조회 [결과가 없으면 null, 둘 이상이면 예외 발생]
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();

        //limit(1).fetchOne()
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        // 페이징 정보 포함, total count 쿼리 추가 실행
        // 복잡하고 성능이 중요한 페이징 쿼리가 필요할 경우는 사용하지 않는것을 권장한다.
        // paging / total 을 분리해 쿼리를 두 번 날리는것을 추천한다.
        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults();

        results.getTotal();
        List<Member> content = results.getResults();

        //count 쿼리로 변경해 count 수 조회
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();

         */
    }
    @Test
    /*정렬 목적
    * 1. 회원 나이 desc
    * 2. 회원 이름 asc
    * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
    * */
    public void sort(){
        em.persist(new Member (null,100));
        em.persist(new Member ("member5",100));
        em.persist(new Member ("member6",100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(),member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);
        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();

    }

    @Test
    public void paging1(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(0)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() {
        // 전체 조회 수 필요 시.


        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }

    @Test
    public void aggregation(){
        // 튜플을 사용하는 이유는 ?
        // 배열의 데이터 타입이 여러가지일 경우 해당 데이터들을 관리하기 위해서는 튜플을 사용해야함.
        // 실무에서는 튜플보다는 DTO 많이 사용함.
        List<Tuple> result = queryFactory.select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /*
        팀의 이름과 각 팀의 평균 연령 구하기.
     */
    @Test
    public void group() throws Exception{

        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                //.having() 사용 가능
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);


        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    // 조인 예시
    @Test
    public void join(){
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) // (Qmember.team, Qteam.team) | leftjoin 등등 다 가능
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1","member2");
    }
    // 세타조인 예시
    // 연관관계가 없어도 join 가능함. 세타조인이라고 함.
    // 조건 : [억지] 회원의 이름이 팀 이름과 같은 회원 조회
    // 특징 : 세타조인 사용 시 outer join 불가능하다. | but, Hibernate -> on 조건을 걸어주면 사용 가능함.
    @Test
    public void theta_join(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member,team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA","teamB");
    }

    // join - on 예시
    // 회원 - 팀 조인, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
    // JPQL : select m, t from Member m left join m.team t on t.name = "teamA"

    // left + on 조인을 사용하지 않고 inner + on 조인과 inner + where 을 사용할 경우는 결과가 같다.
    // 그러므로 inner join 일 경우 where을 사용하도록 하자
    @Test
    public void join_on_filtering(){
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                // .join(member.team, team)
                // .where(team.name.eq("teamA"))
                .fetch();
        for (Tuple tuple : result){
            System.out.println("tuple = " + tuple);
        }
    }

    // 연관관계가 없는 엔티티의 외부조인
    // 회원 이름이 팀 이름과 같은 대상 외부 조인
    @Test
    public void join_on_no_relation(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> result = queryFactory
                .select(member,team)
                .from(member) // 일반조인은 leftjoin(member.team,team) 처럼 엔티티가 두개 들어간다. (연관된 것)
                            // on 조인은 leftjoin(team) <-- leftjoin에서 조인 대상이 바로 들어가고 on에서 원하는 조건이 들어간다.
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        /*
        assertThat(result)
                .extracting("username")
                .containsExactly("teamA","teamB");
         */
        for (Tuple tuple : result){
            System.out.println("tuple = " + tuple);
        }
    }

    // 페치조인 예시
    @PersistenceUnit
    EntityManagerFactory emf; // .isloaded를 사용하기위한 수단. 의미도 없고 별거없음
    @Test
    public void fetchJoinNo(){
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();
        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isFalse();
    }

    //페치조인 예시
    @Test
    public void fetchJoinUse(){
        em.flush();
        em.clear();

        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team,team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 미적용").isTrue();
    }

    /*
    * 서브쿼리 예시
    * 나이가 가장 많은 회원 조회
    *  */
    @Test
    public void subQuery(){

        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(40);

    }

    /*
     * 서브쿼리 예시
     * 나이가 평균 이상인 회원 조회
     * JPA - JPQL 서브쿼리의 한계점으로 from절의 서브쿼리는 지원하지 않는다.
     * 당연하게도 QueryDSL도 지원하지 않는다.
     * [1. 서브쿼리를 join으로 변경(불가능 할 수도) 2. 쿼리를 2번 분리해서 실행 3. nativeSQL사용] 을 통해 해결할 수 있다.
     *  */
    @Test
    public void subQueryGoe(){

        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(30,40);

    }

    /*
     * 서브쿼리 예시
     * where 절 in절 예시
     *  */
    @Test
    public void subQueryIn(){

        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        JPAExpressions
                                .select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(20,30,40);
    }

    /* 서브쿼리
    * select 절 속의 in 예시
    * */
    @Test
    public void selectSubQuery(){

        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.username,
                        JPAExpressions
                                .select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }

    }

    // Case 예시
    // 필요할 일이 많이 없을 것임.
    @Test
    public void basicCase(){

        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    // 이런 복잡한 case 처리는 DB 단계에서 사용하면 안된다.
    // 필요할 때도 있겠지만 DB 단계에서는 최소한의 필터링, 그루핑만 사용할 것.
    // 이 예제로 예를 들면 나이는 그냥 갖고오기만 하고 조회가 아닌 비즈니스로직에서 해당 나이를 판별하는 로직을 설계할 것.
    @Test
    public void complexCase(){
        List<String> result = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    //상수, 문자 더하기 예시
    @Test
    public void constant(){
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = "+tuple);
        }
    }
    // 문자열 더하기 예시
    // member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue()로
    // 문자로 변환할 수 있다. 이 방법은 ENUM을 처리할 때도 자주 사용한다.
    @Test
    public void concat(){
        // {username}_{age} 를 원함.
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    // 일반적인 JPQL Dto 조회방법.
    // DTO를 사용하려면 new operation 을 사용해야함.
    @Test
    public void findDtoByJPQL(){
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username,m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // Querydsl 을 활용한 Dto 조회방법. (property 방식.)
    // 해당 방식은 Getter/Setter가 필요하다.
    // Projections.bean 을 사용하기 위해서는 해당 DTO에 기본 생성자가 존재해야한다. @NoArgsConstructor 를 활용할 것.
    @Test
    public void findDtoByQuerydsl(){
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // Querydsl 을 활용한 Dto 조회방법. (fields 방식.)
    // Projections.fields 를 사용할 때는 Getter/Setter가 필요 없다.
    // 바로 필드에 값을 꽂아준다.
    @Test
    public void findDtoByField(){
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 필드 접근 방법 사용 시 별칭이 다를 경우
    // 프로퍼티나 필드 접근 생성 방식에서 이름이 다를 때 해결방안.
    // .as(" A ") 안해주면 Null로 채워져 반환된다.
    // 서브 쿼리의 경우 ExpressionUtils.as 으로 해줘야한다. "age" 가 서브쿼리의 이름.
    // 자주 쓸 일은 없지만 알아두자.
    @Test
    public void findUserDto(){
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class,
                        member.username.as("name"),
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub),"age")
                ))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("memberDto = " + userDto);
        }
    }

    // Querydsl 을 활용한 Dto 조회방법. (Constructor 방식.)
    // 해당 방법을 사용할 경우 DTO의 필드와 뽑아 낼 데이터의 타입을 맞춰줘야한다.
    @Test
    public void findDtoByConstructor(){
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // QType Dto 예시
    // constructor 방식은 컴파일 오류를 못잡고 런타임 오류만 찾을 수 있다.
    // 해당 방식은 컴파일 오류를 잡을 수 있다.
    // ex) 프로젝션 당시 DTO에 소속되어있지 않은 필드를 선택하였을 때 constructor 방식은 실행 후 오류감지,
    // 해당 방식은 실행 전 오류 감지.
    // 단점: 1: Q파일 생성해야하는 것  2: Dto가 Querydsl에 의존성을 가지게 됨
    @Test
    public void findDtoByQueryProjection(){
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    // 동적쿼리 예시
    // BooleanBuilder 사용 법 1.
    @Test
    public void dynamicQuery_BooleanBuilder(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam,ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);

    }
    // 동적쿼리 예시
    // BooleanBuilder 사용 법 1.
    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        BooleanBuilder builder = new BooleanBuilder();
        if(usernameParam != null){
            builder.and(member.username.eq(usernameParam));
            // usernameParam != null -> builder에 and 조건 추가 한다는 뜻.
            // usernameParam 이 null 이 아닐 경우 검색조건에 추가해 ! 라는 뜻.
        }
        if (ageParam != null){
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    // 동적쿼리 예시
    // where의 다중 파라미터 방식. [where 조건에 null은 무시된다.]
    // 해당 방식이 깔끔하다. 쿼리 가독성이 높아진다. 이거 사용하는거 추천함.
    @Test
    public void dynamicQuery_WhereParam(){
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2(usernameParam,ageParam);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }
    // 동적쿼리 예시
    // where의 다중 파라미터 방식.
    private List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory
                .selectFrom(member)
                .where(allEq(usernameParam,ageParam))
                // where에 null이 들어가면 무시됨.
                .fetch();
    }
    // 동적쿼리 예시
    // where의 다중 파라미터 방식.
    private BooleanExpression usernameEq(String usernameParam) {
        return usernameParam != null ? member.username.eq(usernameParam) : null;

    }
    // 동적쿼리 예시
    // where의 다중 파라미터 방식.
    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null ;
    }
    // 이런식으로 조립할 수도 있음.
    // 이런식으로 사용하면 재사용성도 높아진다.
    private BooleanExpression allEq(String usernameParam, Integer ageParam){
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }

    // querydsl_수정 벌크 연산 예시
    // 주의점. 벌크연산은 영속성 컨텍스트를 무시하고 db에 바로 쿼리를 전송하기 때문에 db와 영속성 컨텍스트가 매칭이 안된다.
    // update 벌크연산 수행 후에는 꼭 flush&clear 하는걸 추천한다. (사실 강제함)
    @Test
    public void bulkUpdate(){
        // member1 = 10 -> 비회원
        // member2 = 20 -> 비회원
        // member3 = 30 -> 유지
        // member4 = 40 -> 유지
        long count = queryFactory
                .update(member)
                .set(member.username,"비회원")
                .where(member.age.lt(28))
                .execute();
        em.flush();
        em.clear();
    }

    // 벌크 연산 예시_ 나이 1살씩 더하기.
    // * = multiply
    @Test
    public void bulkAdd(){
        long addAge = queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();
    }
    //벌크 연산 예시 _ 18살 이상 회원 전체 삭제.
    @Test
    public void bulkDelete(){
        long deleteMember = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }

    // sql-function 호출하기 예시
    // 등록된 function 만 가능
    // 모든 member를 M으로 바꾸기
    @Test
    public void sqlFunction(){
        List<String> result = queryFactory
                .select(Expressions.stringTemplate(
                                "function('replace',{0},{1},{2})",
                                member.username,"member","M"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    // 모든 문자를 소문자로 바꾸기.
    @Test
    public void sqlFunction2(){
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                //.where(member.username.eq(Expressions.stringTemplate("function('lower',{0})", member.username)))
                .where(member.username.eq(member.username.lower())) // querydsl 방식의 lower.
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
