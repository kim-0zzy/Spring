package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/* 왜 static 폴더에 빈 화면을 표시할때 만들어놓은 index.html가 빈 페이지일때 사용되지 않냐면
요청이 오면 스프링 컨트롤러 안에 있는 관련 컨트롤러가 있는지 '먼저' 확인한다. 없으면 static파일을 찾는 순서로 되어있음.
그러나 해당 HomeController에 매핑된 URL이 이미 존재하므로 해당 컨트롤러를 호출하고 static폴더에 index.html까지 찾지 않는다.   */
@Controller
public class HomeController {


    @GetMapping("/")
    public String home(){
        return "home";
    }
}

