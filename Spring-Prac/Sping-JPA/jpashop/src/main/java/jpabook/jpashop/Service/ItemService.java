package jpabook.jpashop.Service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {


    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }
    //아래는 변경감지 예시 updateItem 메서드. 영속성 컨텍스트에서 엔티티를 다시 조회 후 데이터를 수정하는 방식.
    // 보통은 set을 남발하지 않고 의미있는 메서드를 만들어서 사용한다.
    // 실무에서는 setter를 사용하지 않음을 알아두자. ex) xx.change(parm1, parm2...) / xx.addStock()
    //+ 가급적 Controller에서 엔티티를 생성하지 말자.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);

    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }
    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }

}
