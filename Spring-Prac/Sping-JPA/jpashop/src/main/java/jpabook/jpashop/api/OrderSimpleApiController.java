package jpabook.jpashop.api;


import jpabook.jpashop.Service.OrderSearch;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// -설계목적-
// order 조회, order -> member 연관 / order -> derlivery 연관
// xToOne 관계 성능 최적화
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    // 어찌됐든 엔티티를 노츨하면 안됨. 간단한 조회라도 안됨.  *@JsonIgnore로 무한로딩을 해결해도 오류가 발생하는 이유? memo-19 *
    // 이렇게 하면 무한루프를 돌게되는 문제가 생김. 이걸 해결한 첫 번째 방법은 양방향 연관관계중 한 쪽에 @JsonIgnore를 걸어주는 것.
    // 두 번째 방법은 1방법+ build.gladle에 Hibernate5Module을 설치해 주는 것. (jpashopapplication 클래스 참고.) (* * 해결책.)
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllCriteria(new OrderSearch());
        //세 번째 방법, 1,2방법 + 원하는 정보만 선택해서 Lazy 강제 초기화를 하는 방법.
        for(Order order: all){
            order.getMember().getName();
            order.getDelivery().getAddress(); // --> Lazy 강제초기화 방법.
            //어떤 방법을 사용하던 Lazy -> Eager 변경을 하면 안됨.
            //즉시 로딩때문에 연관관계가 필요 없는 경우에도 데이터를 항상 조회하기 때문에 성능이 저하됨
        }
        return all;
        // 결국 어떤 방법을 사용하던 엔티티를 직접 노출하면 여러가지 문제가 있으므로 사용하지 않는 것을 권장함.
        // 이 모든 것은 엔티티를 노출하기 때문에 발생하는 문제임.
    }
    // v1보다 v2처럼 Dto를 이용하는 것이 무조건적으로 좋음.
    // 그러나 v1,v2모두 Lazy로딩으로 인한 데이터베이스 쿼리가 너무 많이 호출되는 문제가 있음. (N+1 문제)
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllCriteria(new OrderSearch());
        // o = orders = <Order>
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }
    // OrderRepository의 findAllWithMemberDelivery 메서드
    // * orderRepository.findAllWithMemberDelivery() * .Lazy로딩으로 설정했지만 Lazy로딩을 무시함. *
    // 이렇게 하면 V2와 결과는 같지만 날라가는 쿼리의 양이 다르다. 성능또한 더 좋다
    // ★★★★★★★★★★매우중요함★★★★★★★★★★★★★★★
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o-> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // v4에서는 select절에서 가져오고싶은 데이터 선택하기에 v3보다 select절의 양과 네트워크 소비가 줄었다. 그러나 크게 차이나지 않음.
    // However, v3와 v4의 성능에는 우열을 가리기 어렵다. 메커니즘이 다르기 때문.
    // v3는 재사용성이 높고 v4는 낮다. 코드는 v3가 우세 쿼리는 v4가 우세.
    // repository는 엔티티의 객체그래프를 조회하는 등의 역할을 해야하지만 v4처럼 사용하면
    // api의 스펙에 맞춰 repository의 코드를 짜버린것임. 재사용성이 낮음.
    // v4의 유지보수성을 올리려면 하위폴더를 만들어 repository는 순수하게 조회의 기능만 수행 할 수 있도록 repository와 분리시킨다.
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){

        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data   //DTO가 엔티티를 받는것은 괜찮음.
    static class SimpleOrderDto{
        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // Lazy 초기화
        }
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
    }



}
