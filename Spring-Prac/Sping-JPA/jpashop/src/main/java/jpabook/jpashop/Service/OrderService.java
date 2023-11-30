package jpabook.jpashop.Service;


import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service`
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //주문
    @Transactional
    //Long memberId 대신 Member엔티티를 파라미터로 받게 되면
    // OrderController의 order메서드의 트랜젝션의 영속성 컨텍스트와 관계 없는 Member엔티티가 넘어오게 되기때문에 안씀.
    public Long order(Long memberId, Long itemId, int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성 (예제 단순화를 위해 단일품목 선택기능만 넣음)
        OrderItem orderItem = OrderItem.createOrderItem(item,item.getPrice(),count);

        /* 이런식으로 개별필드를 만들어서 값을 다 채운는 방식은 권장하지 않음.
        해당 행위를 막아놓기 위해 OrderItem의 생성자를 protected로 만드는 것을 권장함.
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setCount();
        */

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem /*, orderItem1 ... 등을 넣으면 추가주문 가능 */);

        //주문 저장
        //persist를 order 하나만 한 이유 : Order엔티티의 OrderItem과 Delivery에 CascadeType.ALL 옵션이 설정되어 있기 때문.

        orderRepository.save(order);


        return order.getId();
    }

    //취소

    @Transactional
    public void cancelOrder(Long orderId){
        //주문엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문취소
        order.cancel();
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllCriteria(orderSearch);
    }




}
