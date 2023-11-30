package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.Entity;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

//                                                     ↓ <엔티티명, Pk의 Type>
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // ★ 중요하다는건 아닌데 엄청 좋은 기능임. [쿼리 메소드 기능 / 3번째 강의 (5분)] @Param은 파라미터 바인딩
    @Query("select m from Member m where m.username = :username and m.age =:age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //사용자 이름을 리스트로 전부 다 가져오기
    @Query("select m.username from Member m")
    List<String> findUsernameList();

    //Dto 반환하는 방법.
    @Query("select new study.data-jpa.dto.MemberDto (m.id,m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    //컬렉션에 in절 사용하는 방법.
    @Query("select m from Member m where m.username in :names")
    List<MemberDto> findByNames(@Param("names") List<String> names);

    // ####  1. 컬렉션  2.  단건   3.  단건 옵셔널
    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m") //이런식으로 counterQuery를 분리할 수 있다
    //페이징 방법                                                  카운터쿼리를 분리하는 이유는? 페이지가 많아질수록 성능이 떨어지기 때문에.
    Page<Member> findByAge(int age, Pageable pageable);             // 카운터쿼리를 미리 생성하지 않기 위해
    //사용법
    // 1. PageRequest 객체 생성 -> PageRequest pageRequest = PageRequest.of([ ]);
    // 2. 조건 입력 [_page:_0, _size:_3, Sort.by(Sort.Direction.DESC), _properties:_"username"]
    // 3. 객체 바탕으로 호출 ex) Page<Member> page = memberRepository.findByAge(age,pageRequest);
    // 이 후 JPA에서 알아서 TotalCount 쿼리까지 자동으로 같이 생성함. why? 반환타입이 Page이니깐. page.getTotalElements(); 으로 가져올 수 있음.
    // List<Member> content = page.getContent(); 으로 객체 내 내용물 뽑아올 수 있음.
    // .getNumber() 으로 페이지 번호도 가져올 수 있음.
    // .getTotalPages() 전체 페이지 수 , .isFirst() 첫번째인지 , .hasNext() 다음페이지 있는지 등등
    // Slice로 받고싶으면 반환타입도 Slice로 바꿔주기.
    // ★ 3번처럼 사용 시 엔티티가 외부에 노출되는 사태가 벌어지기 때문에 DTO로 변환해서 사용하기를 권장함.
    // DTO로 변환하는 방법 : Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam()));

    //S-DataJpa의 벌크성 업데이트
    @Modifying(clearAutomatically = true) // <- clearAutomatically 는 벌크연산 수행 후 영속성 컨텍스트 초기화 역할
    // ↑ .executeUpdate() 의 역할, 이것이 없으면 @getResult(); 를 실행해버림 = 에러남
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
    // JPA의 벌크성 연산 시 주의해야할 점
    // 벌크 연산 수행 시 영속성 컨텍스트를 무시하고 DB에 바로 적용시켜버림, 영속성 컨텍스트에 반영 X
    // 그러므로 벌크연산 수행 후 바로 em.flush()와 em.clear()를 시켜줘야함. -> 영속성 컨텍스트 초기화 역할

    // 페치조인
    @Query("select m from Member m left join fetch m.team")
    List<Member> fineMemberFetchJoin();

    // 페치조인을 엔티티그래프로 사용하는 법.
    // ※ 엔티티 그래프를 사용하면 페치조인을 편리하게 사용할 수 있다. (엔티티그래프는 JPA 표준이긴함)
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // 이런방식으로도 가능.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 이것도 가능.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // JPA 쿼리 힌트 (SQL 힌트가 아니라 JPA 구현체에게 제공하는 힌트)
    // 이걸 사용하면 .get을 사용하긴 하지만 읽기 기능만 하기 때문에 변경감지에 탐지되지 않음
    @QueryHints(@QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // Lock은 Jpa소속의 기능. JPA책의 Transaction&Lock 파트 읽어보기.
    // 많이 사용하진 않을것이라 말했음.
    // 실시간 트래픽이 많은 서비스에서는 Lock을 사용하면 안된다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

    // UsernameOnly 참조              자료의 Projections 확인. P.64
    List<UsernameOnly>findProjectionsByUsername(@Param("username") String username);
    // UsernameOnlyDto 참조
    //List<UsernameOnlyDto>findProjectionsByUsername(@Param("username") String username);


    //네이티브 쿼리           MemberRepositoryTest Gogo
    @Query(value = "select * from member where username =?", nativeQuery = true)
    Member findByNativeQuery(String username);

    //네이티브의 projections 활용          MemberRepositoryTest Gogo
    @Query(value = "select m.member_id as id, m.username, t.name as teamName"+
            "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);

}
