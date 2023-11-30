package jpabook.jpashop.Controller;


import jpabook.jpashop.Service.ItemService;
import jpabook.jpashop.Service.MemberService;
import jpabook.jpashop.Service.OrderSearch;
import jpabook.jpashop.Service.OrderService;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items",items);

        return "order/orderForm";
    }

    @PostMapping("/order") //@RequestParam는 해당 html파일에서 ("xx")로 지정해놓은 값을 가져올 수 있음.
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){

        orderService.order(memberId, itemId, count);
        return "redirect:/orders";
    }

    @GetMapping("/orders") //컨트롤러에서 리포지토리 조회의 경우 단순위임기능이면 허용을 하는 편
                            //@ModelAttribute 기능 궁금하면 찾아보기. https://donggu1105.tistory.com/14
                            // 해당 어노테이션을 사용한 파라미터의 클래스는 bean타입 클래스여야 하고 get-setter가 있어야 하며
                            // 실행시 객체가 자동으로 생성되며 웹으로 넘어간 값들이 자동 바인딩 된다.
                            // 또 객체가 자동으로 Model 객체에 추가되고 뷰로 전달된다.
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
