package jpabook.jpashop.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    //@JsonIgnore 이것으로 api에서의 노출을 막을 수 있음.
    @JsonIgnore
    @OneToMany(mappedBy = "member") //회원 1:N 주문
    private List<Order> orders = new ArrayList<>();
}
