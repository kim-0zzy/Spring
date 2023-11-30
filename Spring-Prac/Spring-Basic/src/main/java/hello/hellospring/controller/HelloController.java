package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello") // ← 웹어플리케이션에서 /hello가 들어오면 해당 메소드를 호출해줌
    public String hello(Model model){
        model.addAttribute("data", "hello!!"); // data = Key / hello!! = Value
        //윗줄의 "data"는 hello.html의 ${키값(data)} 으로 호출 할 수있음.
        return "hello";
        // 윗줄의 return hello;는 resouces/templates/hello.html을 처리하라는 뜻.
    }

    @GetMapping("hello-mvc") //(@RequestParam("name") 이 부분은 name 파라미터를 넘겨달라는 뜻.
                            // 기본적으로는 값을 넘겨주어야함. ex) 주소창에 메소드이름 ?파라미터 이름= 값
                                                            // localhost:8080/ hello-mvc?name=fuck u
    public String helloMvc(@RequestParam("name") String name, Model model){
        model.addAttribute("name", name);
        return "hello-template";
    }

    @GetMapping("hello-string")
    @ResponseBody // <-- 이것의 의미는 html의 바디태그가 아니라 http의 바디부분에 데이터를 직접 그대로 넘겨주겠다는 뜻. (밑의 "hello " + name)
    public String helloString(@RequestParam("name") String name){
        return "hello "+ name;
    }

    // Json 방식. Json이 뭔지 알아보기. (key-value로 이뤄진 구조. 딕셔너리랑 비슷한거같음.)
    @GetMapping("hello-api")
    @ResponseBody //요즘은 객체로 반환할 때에는 Json을 사용하는게 일반적임.
    // @ResponseBody를 사용하면 위에서 말했듯이 http 바디에 문자내용을 직접 반환.   # Api방식은 객체반환 방식.
    // 그런데 @ResponseBody를 사용했는데 반환대상이 객체라면 viewResolver대신에 httpmessageConverter가 동작,
    // 문자처리는 StringhttpM-C 객체처리는 MappingJackson2http M-C (JsonConverter)가 처리함. 스프링 내 국룰로 정해져있음.
    // 근데 요즘은 JSON만 사용함.
    public Hello helloApi(@RequestParam("name") String name){
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }
    static class Hello{
        private  String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

}
