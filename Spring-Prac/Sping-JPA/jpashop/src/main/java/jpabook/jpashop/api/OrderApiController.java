package jpabook.jpashop.api;


import jpabook.jpashop.Service.OrderSearch;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // 엔티티를 조회해서 그대로 반환하는 버전.
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());
            // 객체 그래프 강제 초기화 구문 / 강제초기화 이유↓
            // 프록시는 가짜 엔티티객체를 가지고 있기 때문에 데이터를 받고싶으면 강제로 프록시 초기화를 해서 영속성컨텍스트를 불러와야함.
        }
        return all;
    }
    // 엔티티를 조회 후 DTO로 변환해서 반환하는 방법.
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){

        List<Order> orders = orderRepository.findAllCriteria(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o-> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
    // V2에 페치조인을 더한 방법. but, 컬렉션에 페치조인이라 페이징이 불가능함.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> result = orders.stream()
                .map(o-> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    // V3방식에 추가로 xToOne관계는 fetch조인, 컬렉션은 지연로딩(이렇게 해야 페이징 쿼리에 영향없음.)을 사용한 방식, 페이징이 가능해짐.
    // 이거 사용하려면 .yml가서 default_batch_fetch_size추가해야함 (@BatchSize를 개별추가해도 됨.)
    // 이 옵션을 사용하면 컬렉션이나 프록시 객체를 설정한 사이즈만큼 in쿼리로 땡김.
    // 장점 : N+1문제 해결, 조인보다 DB전송량 최적화 됨, 쿼리호출수가 증가하지만 DB데이터 전송량 감소, 페이징 가능
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam( value = "offset", defaultValue = "0") int offset,
            @RequestParam( value = "limit", defaultValue = "100") int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
                .map(o-> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    // JPA에서 DTO를 직접 조회하는 방식.
    // OrderQueryRepository 의 패키지를 분리한 이유는
    // 엔티티의 Repository는 엔티티를 조회할 때 사용하는 것이고 (핵심 비즈니스),
    // 쿼리 Repository는 화면이나 Api의 의존관계가 있는 엔티티가 아닌 특정 화면에 의존적인 것을 조회하려고 분리하는 것.
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    // 컬렉션 조회 최적화. xToMany 컬렉션은 IN쿼리 사용
    // 쿼리는 루트1번 컬렉션1번
    // xToOne관계는 먼저 조회하고, 여기서 얻은 식별자 orderId로 xtoMany관계인 OrderItem을 한꺼번에 조회(in 쿼리 사용)
    // + MAP을 사용해 매칭 성능 향상
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }

    // 1번의 쿼리로 모든 데이터를 가져오는 방식, 데이터 중복의 가능성 내제
    // V5보다 느릴수도 있고 페이징이 불가능하며 애플리케이션에서 큰 추가작업이 필요하다.
    // 쿼리가 적다고 무조건 항상 좋은게 아니라는 것을 알아두자.
    // 반환타입이 OrderFlatDto라서 api스펙이 맞지 않음, 그러나 노가다의 힘으로 OrderQueryDto로 변환가능함
    // 노가다란? 내가 직접 중복을 배제하는 방식.
    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> orderV6(){
        return orderQueryRepository.findAllByDto_flat();
    }
    //권장순서 : 1 → 3 → 3.1(페이징 필요 시) → 4 → Native_Sql or 스프링_JDBCTemplate
    // 왜? 1 방식은 페치조인이나 @BatchSize등 코드수정 없이 옵션변경으로만 성능최적화를 시도할 수 있다.
    // Jpa에서 dto조회하는 방식은 많은 코드를 변경해야한다.
    // but, 엔티티 조회방식에 캐시를 사용하면 안됨, 무조건 Dto로 변환 후 사용해야함.
    // 4-5-6에서 성능우열은 가릴 수 없다. 4는 코드가 단순하고 유지보수가 쉽고 단건조회에 유리하고
    // 5는 코드가 복잡한 대신 다건 조회 시 상황에 따라 V4보다 100배 이상의 성능이 발휘 될 수 있다.
    // 6는 완전 다른 방법이다. 추천하지 않는다.


    @Getter //V2방식
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        // 매번 말했듯이 엔티티를 직접 노출하면 안됨 (OrderItem 엔티티 노출 상황.)
        // 어떻게 해결? OrderItem도 Dto로 변환.
        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            // orderItems = order.getOrderItems(); ↓ 아래처럼 Dto로 만들어서 반환해줄 것. (OrderItemDto 클래스)
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }
    @Getter
    static class OrderItemDto{
        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
        private String itemName; //상품명
        private int orderPrice;  //주문가격
        private int count;       //주문수량

    }
}
