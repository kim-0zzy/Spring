package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;


//filter 생성 방법.
@Slf4j
public class LogFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        // ServletRequest와 ServletResponse를 사용할 땐 HttpServletRequest로 다운 캐스팅 필수.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}] [{}]", uuid, requestURI);
            chain.doFilter(request,response); // 이거 안찍으면 프로그램 먹통됨. 꼭 찍어줘야함 그래야 다음 필터로 넘어감
        }catch (Exception e){
            throw e;
        }finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
