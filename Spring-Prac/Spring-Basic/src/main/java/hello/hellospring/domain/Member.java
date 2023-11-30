package hello.hellospring.domain;

import javax.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue는 db에 값을 자동으로 생성해주는 아이덴티티전략이라고 한다.
    private Long id; // 구분하기 위해 시스템이 저장하는 ID
    private String name; // 고객이름

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
