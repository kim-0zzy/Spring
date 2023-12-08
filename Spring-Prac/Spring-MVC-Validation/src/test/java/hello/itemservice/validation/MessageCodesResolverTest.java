package hello.itemservice.validation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public class MessageCodesResolverTest {

    MessageCodesResolver codesResolver = new DefaultMessageCodesResolver();

    @Test
    void messageCodesResolverObject(){
        String[] strings = codesResolver.resolveMessageCodes("required", "item");
        for (String string : strings) {
            System.out.println("string = " + string);
        }
        Assertions.assertThat(strings).containsExactly("required.item", "required");
    }

    @Test
    void messageCodesResolverFiled(){
        String[] codes = codesResolver.resolveMessageCodes("required", "item", "itemName", String.class);
        for (String code : codes) {
            System.out.println("code = " + code);
        }

        Assertions.assertThat(codes).containsExactly(
                "required.item.itemName",
                "required.itemName",
                "required.java.lang.String",
                "required");
    }



}
