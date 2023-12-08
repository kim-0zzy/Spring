package hello.itemservice.domain.item;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총 금액은 10,000이 넘어야합니다. ")
// -> 이건 너무 제약이 많고 복잡하다. 실무에서 사용이 어렵다.
// 그렇기에 Global Error 는 Java Code로 해결 하는 것을 추천함.
public class Item {

//    @NotNull // 수정 요구사항 추가
//    @NotNull(groups = UpdateCheck.class) //V3
    private Long id;

//    @NotBlank(message = "공백X", groups = {SaveCheck.class, UpdateCheck.class}) //V3
    private String itemName;

//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class}) //V3
//    @Range(min = 1000, max = 1000000 , groups = {SaveCheck.class, UpdateCheck.class}) //V3
    private Integer price;

//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class}) //V3
//    @Max(value = 9999, groups = {SaveCheck.class}) // 수정 요구사항 추가 //V3
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
