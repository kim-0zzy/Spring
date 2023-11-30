package jpabook.jpashop.Controller;

import jpabook.jpashop.Service.ItemService;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form",new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        // 해당 생성 메서드를 만들어 사용하는 것이 좋은 설계. Setter는 다 제거하는것이 좋다.
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items",items);
        return "items/itemList";
    }

    @GetMapping("items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId")Long itemId, Model model){
        Book item = (Book)itemService.findOne(itemId); // 캐스팅을 사용하는 것은 권장하지 않음. 예제니까 사용하는 것.

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("items/{itemId}/edit") // @ModelAttribute("XX")는 html파일에서 XX로 잡아놓은 데이터가 그대로 넘어올 수 있게 해줌.
    public String updateItem(@PathVariable Long itemId , @ModelAttribute("form") BookForm form){
        //Book book = new Book();
        //book.setId(form.getId());
        //book.setName(form.getName());
        //book.setPrice(form.getPrice());
        //book.setStockQuantity(form.getStockQuantity());
        //book.setAuthor(form.getAuthor());
        //book.setIsbn(form.getIsbn());
        //itemService.updateItem(book);
        // 가급적 Controller 에서 엔티티를 생성하지 말자.
        // 위와같이 엔티티를 생성해서 업데이트하는것은 권장하지 않음. 아래 방법이 권장됨.
        // 트랜젝션이 있는 서비스 계층에서 식별자와 변경할 데이터를 명확히 전달, (파라미터 or Dto)
        itemService.updateItem(itemId,form.getName(),form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }
}
