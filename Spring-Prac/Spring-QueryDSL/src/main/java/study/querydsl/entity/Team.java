package study.querydsl.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id","name"}) // 사용시 클래스가 소유한 필드만 넣을 것.
// ↑ 객체가 가지고 있는 정보나 값들을 문자열로 만들어 리턴하는 메서드 ToString()을 대체해주는 어노테이션
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

}
