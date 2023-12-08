package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {
// ExceptionResolver 는 예외 상태 코드를 변환해서 사용할 수 있도록 도와줌. -> 서블릿 내에서 오류를 처리할 수 있도록 도와줌
// ModelAndView에 값을 채워 새로운 오류 페이지를 렌더링해 보여줄 수도 있음.
// API 오류 시 JSON으로 응답을 처리해줄 수도 있음. -> response.getWriter().println({"aa" : "aa"})
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        log.info("call resolver", ex);

        try {
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage()); // <-- 사용하면 컨트롤러에서 발생한 예외를 먹어버림.
                return new ModelAndView(); // <-- 사용하면 아무것도 없는 MAV를 반환함. 그러면 랜더링 할게 없으니까 아무것도 반환하지 않고 WAS로 처리 흐름이 넘어감.
                                                // 그러면 윗줄에서 sendError를 호출했으니 WAS는 에러 페이지를 뒤져서 반환해줌. (물론 페이지 지정해서 반환 할 수 있음)
                                            // 근데 null을 반환하면 다음 ExceptionResolver를 찾아 실행함. 만약 처리 불가능하면 예외처리 없이 서블릿 밖으로 예외를 던짐.
            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
