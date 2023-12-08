package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;

    // @InitBinder 사용 시 해당 컨트롤러가 요청 될 때 마다 WebDataBinder가 만들어지고 컨트롤러 소속 메서드들이 호출될 때 마다
    // 검증기가 작동함.
    @InitBinder
    public void init(WebDataBinder dataBinder){
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addFormV2";
    }

//    @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // @ModelAttribute Item item  <- Get에서 빈 값을 넘기고 Post에서 @ModelAttribute를 사용하면
        // 오류 발생 시 전에 입력했던 값이 그대로 담김. 이라고 했으나
        // BindingResult를 사용할 경우 해당 값이 유지가 안된다. 해결책은 addItemV2로


        // 이전 errors Map에서 bindingResult로 변경.
        // !주의! BindingResult의 위치는 오류를 담을 객체 바로 뒤에 와야한다. ex) Item

        //(특정 필드) 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000 이상이어야 합니다. 현재 값 = " + resultPrice));
                // global error는 new ObjectError로 대체함.
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //성공 로직

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 해결책은 BindingResult의 2번째 생성자를 사용하면 해결가능하다.


        //(특정 필드) 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
//            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null,null, "상품 이름은 필수 입니다."));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
//            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
//            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999까지 허용합니다."));
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item",null, null, "가격 * 수량의 합은 10,000 이상이어야 합니다. 현재 값 = " + resultPrice));
                // global error는 new ObjectError로 대체함.
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //성공 로직

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // bindingResult의 FieldError의 생성자 파라미터 중 code가 String 배열 타입인 이유는,
        // 첫 번째 원소를 못찾을 경우 다음 원소를 채택해 출력해주기 때문. ( 거기서도 못찾으면 defaultMessage로 출력 )
        // Object는 포맷팅 용 값


        //(특정 필드) 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
//            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수 입니다."));
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"},null, null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
//            bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 10000}, null));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
//            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999까지 허용합니다."));
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999},  null));
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
                // global error는 new ObjectError로 대체함.
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //성공 로직

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // addError -> rejectValue 로 교체. 훨씬 간단해짐
        // 왜 교체? bindingResult는 자신의 타겟이 무엇인지 이미 알고 있음. 그래서 타겟의 정보를 적어줄 필요는 없음.
        // 레벨 단계의 오류 메시지 설계 예시 -> errors.properties
        // errors.properties의 하단을 보면 스프링이 만들어준 defaultMessage의 처리방법도 설정할 수 있음.

        // 바인딩 오류 시 바로 새로고침
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //(특정 필드) 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
//            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"},null, null));
            bindingResult.rejectValue("itemName", "required");
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
//            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 10000}, null));
            bindingResult.rejectValue("price", "range",new Object[]{1000, 10000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 9999){
//            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999},  null));
            bindingResult.rejectValue("quantity", "max", new Object[]{9999},  null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if(item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
//                bindingResult.addError(new ObjectError("item",new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //성공 로직

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

//    @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // 바인딩 오류 시 바로 새로고침
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //(특정 필드) 검증 로직
        itemValidator.validate(item, bindingResult);

        //성공 로직
        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        // 바인딩 오류 시 바로 새로고침
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        //(특정 필드) 검증 로직
        //@Validated 사용 시 Item에 대한 Validation이 자동으로 수행됨.
        // -> 아래의 메서드가 자동 호출됨.
//        @InitBinder
//        public void init(WebDataBinder dataBinder){
//            dataBinder.addValidators(itemValidator);
//        }

        //성공 로직
        //검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors ={}", bindingResult);
            return "validation/v2/addFormV2";
        }

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

