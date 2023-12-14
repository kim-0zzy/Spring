package practice.typeConverter.formatter;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import practice.typeConverter.converter.IpPortToStringConverter;
import practice.typeConverter.converter.StringToIntegerConverter;
import practice.typeConverter.converter.StringToIpPortConverter;
import practice.typeConverter.type.IpPort;

import static org.assertj.core.api.Assertions.assertThat;

public class FormattingConversionServiceTest {

    @Test
    void formattingConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        // 컨버터
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        // 포메터
        conversionService.addFormatter(new MyNumberFormatter());

        // 컨버터 사용
        IpPort ipPort = conversionService.convert("127.0.0.1:8080", IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));

        // 포메터 사용
        assertThat(conversionService.convert(1000, String.class)).isEqualTo("1,000");
        assertThat(conversionService.convert("1,000", Long.class)).isEqualTo(1000L);

    }
}
