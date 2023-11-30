package study.datajpa.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id","username","age"})
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    //연관관계 편의 메서드
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this); //Team클래스에 있는 Member에 대한 셋팅 메서드
    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team!=null){
            changeTeam(team);
        }
    }

    /*
    public void changeUserName(String username){
        this.username = username;
    }*/
}