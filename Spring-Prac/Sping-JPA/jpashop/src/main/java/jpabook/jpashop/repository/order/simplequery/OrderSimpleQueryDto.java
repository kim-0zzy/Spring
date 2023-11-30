package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data  //Dto는 엔티티를 참조해도 괜찮다.
public class OrderSimpleQueryDto {


    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    //public OrderSimpleQueryDto(Order order ...생략) 이렇게 엔티티를 반환하게 되면 jpa가 왔다갔다 할 때 order를 식별자로 넣어버리게됨.
    // 이렇게 바꿔주어야함
    public OrderSimpleQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }

}
