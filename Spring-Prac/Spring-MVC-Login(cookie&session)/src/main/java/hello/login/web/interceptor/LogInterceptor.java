package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";
    // preHandle과 postHandle은 예외인 상황에서는 실행이 안된다.
    // afterCompletion는 항상 호출된다.



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid); // uuid를 해당 클래스 다른 메서드에서도 사용할 수 있게 하는 방법.
                                            // Filter에서는 같은 메서드에서 호출하기에 문제가 없지만 여긴 분리되어있음.

        // @RequestMapping 사용 시 : HandlerMethod 사용
        // 정적 리소스 사용 시 : ResourceHttpRequestHandler 사용
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; // 호출 할 컨트롤러 메서드의 모든 정보가 포함되어 있다. ( 현재 접속요청한 URL을 매핑한 컨트롤러 )
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);
        log.info("Response [{}][{}][{}]", logId, requestURI, handler);

        if (ex != null) {
            log.error("afterCompletion error !!", ex);
        }

    }
}
