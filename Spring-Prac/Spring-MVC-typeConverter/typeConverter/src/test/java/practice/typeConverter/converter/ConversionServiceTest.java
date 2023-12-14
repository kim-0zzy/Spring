package practice.typeConverter.converter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import practice.typeConverter.type.IpPort;

import static org.assertj.core.api.Assertions.*;

public class ConversionServiceTest {

    @Test
    void conversionService() {
        // 등록 -> 원래는 Configuration 같은 클래스에 넣고 사용해야함. ISP 원칙 고수.
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        //사용
        Integer result1 = conversionService.convert("10", Integer.class);
        System.out.println("result1 = " + result1);
        assertThat(result1).isEqualTo(10);

        String result2 = conversionService.convert(10, String.class);
        System.out.println("result2 = " + result2);
        assertThat(result2).isEqualTo("10");

        IpPort result3 = conversionService.convert("127.0.0.1:8080", IpPort.class);
        System.out.println("result3 = " + result3);
        assertThat(result3).isEqualTo(new IpPort("127.0.0.1", 8080));

        String result4 = conversionService.convert(new IpPort("127.0.0.1", 8080), String.class);
        System.out.println("result4 = " + result4);
        assertThat(result4).isEqualTo("127.0.0.1:8080");

    }
}
