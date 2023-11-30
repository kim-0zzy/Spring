package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;


    public List<OrderSimpleQueryDto> findOrderDtos(){

        // 원래코드
/*        return em.createQuery(
            "select o from Order o" +
                " join o.member m"+
                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList(); */
        // select o 부분을 보면 Order o가 셀렉트 되었는데 이러면면 OrderSmpleQueryDto가 매핑 될 수가 없음.
        // 해당 JPQL은 dto가 생성자에 들어가서 매핑되고 그러는 방식이 아님. Jpa는 엔티티나 value Object(embedable)만 반환할 수 있음.
        // 그래서 dto를 반환하려면 new 오퍼레이션을 꼭 써주어야함.
        // new 오퍼레이션에서 엔티티를 파라미터로 바로 넘겨주면 안됨, 엔티티가 식별자로 넘어가기 떄문. OrderSimpleQueryDto 참고.
        // new 오퍼레이션을 사용하면 JPQL의 결과를 DTO로 즉시 변환 할 수 있음.
        // 어떻게 보면 이 방식은 논리적으로 계층이 깨져있는 상태임. repository에 화면을 의존하고 있는 것.
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                                " from Order o"+
                                " join o.member m"+
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}


// 쿼리방식 선택 권장 순서 (XtoOne 방식 위주?)
// 1. 엔티티를 DTO로 변환하는 방법 선택                                    V2
// 2. 필요하면 페치조인 사용해 성능 최적화 -> 95%가량의 문제 해결 가능          V3
// 3. 그래도 안되면 DTO로 직접 조회하는 방법 사용                            V4
// 4. 최후로 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template를 사용해 SQL를 직접 사용.