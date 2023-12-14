package practice.typeConverter;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import practice.typeConverter.converter.IntegerToStringConverter;
import practice.typeConverter.converter.IpPortToStringConverter;
import practice.typeConverter.converter.StringToIntegerConverter;
import practice.typeConverter.converter.StringToIpPortConverter;
import practice.typeConverter.formatter.MyNumberFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 주석처리 우선순위 ( 이 두개는 숫자<->문자 처리인데 아래 포매터도 동일 기능. 이걸 먼저 등록하면 포매터 동작 X )
//        registry.addConverter(new StringToIntegerConverter());
//        registry.addConverter(new IntegerToStringConverter());

        registry.addConverter(new StringToIpPortConverter());
        registry.addConverter(new IpPortToStringConverter());

        registry.addFormatter(new MyNumberFormatter());

    }
}
