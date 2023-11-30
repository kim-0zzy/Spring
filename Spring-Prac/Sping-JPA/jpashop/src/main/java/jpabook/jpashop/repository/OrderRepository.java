package jpabook.jpashop.repository;


import jpabook.jpashop.Service.OrderSearch;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }


    public List<Order> findAllByString(OrderSearch orderSearch){

        // 동적쿼리의 첫번째 방법. 문자열더하기를 사용하는 것.
        // but 존나 복잡함, 추천안함, 버그 ㅈㄴ많음
       String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;
        // 주문상태 검색
        if(orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpql +=" where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql = " o.status = :status";
        }
        // 회원이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql +=" and";
            }
            jpql += "m.name like :name";
        }

        TypedQuery<Order> query =  em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null){
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();

        /* 이렇게 쓰면 동적쿼리가 아님.
        return em.createQuery("select o from Order o join o.member m" +
                "where o.status = :status" +
                "and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)
                .getResultList();
        */
    }
    // JPA의 동적쿼리 빌드전용 라이브러리, but 권장하지않음. 실무에서 사용하기 힘듬.
    // QueryDSL을 추천함.
    public List<Order> findAllCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb =  em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();


        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name =
                    cb.like(m.<String>get("name"),"%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        em.createQuery(cq).setMaxResults(1000);
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    // OrderSimpleApiController 의 orders v3부분
    // 페치조인으로 써야할 DB를 Lazy로딩을 무시하고 한번에 가져옴.
    // ★★★★★★★★★★★★★매우 중요함★★★★★★★★★★★★★
    public List<Order>findAllWithMemberDelivery(){
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m"+
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    // 이거 사용하려면 .yml 파일 가서 default_batch_fetch_size 추가해야함
    public List<Order>findAllWithMemberDelivery(int offset, int limit){
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m"+
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // 컬렉션 페치조인 1:N 상황에서는 사용X 권장
    // distinct 없이 사용하면 DB에 조인이 되어서 로그가 2배가 되어버림. (db에 키워드를 날려줌, (from 뒤의) 엔티티가 중복일 경우 걸러줌)
    // x대다 관계에서 다쪽에 맞춰버리기 때문에 레퍼런스값까지 똑같은 조회를 2배로 뻥튀기해버림.
    // 그러나 db쿼리에서 db결과를 뽑을때는 이전과 같이 2배뻥튀기 되어있음. 왜? db에서 distinct는 모든값이 동일해야 하기 때문.
    // 단점 첫 번쨰는 페이징 불가능. 메모리에서 페이징 처리해버림. out of memory 발생. 쓰면 큰일남.
    // 두 번쨰는 컬렉션 페치조인은 1개만 사용 가능, 둘 이상에 사용시 1:N:M이 되어 N*M 상황이 펼처짐.
    public List<Order> findAllWithItem(){
        return em.createQuery(
                "select distinct o from Order o"+
                " join fetch o.member m"+
                " join fetch o.delivery d"+
                " join fetch o.orderItems oi"+
                " join fetch oi.item i",Order.class)
                .getResultList();
    }

}
