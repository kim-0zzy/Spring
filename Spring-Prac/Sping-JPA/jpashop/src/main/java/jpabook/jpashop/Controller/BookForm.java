package jpabook.jpashop.Controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    private Long id;
    //상품 공통속성
    private String name;
    private int price;
    private int stockQuantity;
    //책관련 속성
    private String author;
    private String isbn;

}
