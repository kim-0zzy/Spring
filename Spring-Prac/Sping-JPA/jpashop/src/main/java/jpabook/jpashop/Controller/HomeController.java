package jpabook.jpashop.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //로그 남기는 기능
public class HomeController {


    @RequestMapping("/")
    public String home(){
        log.info("home Controller");
        return "home";
    }


}
