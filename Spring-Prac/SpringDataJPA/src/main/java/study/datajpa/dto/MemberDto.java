package study.datajpa.dto;


import lombok.Data;
import study.datajpa.entity.Member;

@Data
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        //this.teamName = member.getTeam().getName();
    } // DTO는 엔티티를 그대로 받아도 전혀 지장없기 때문이 이렇게 해도 된다.
    // 이렇게하면 실사용시 코드가 줄어든다는 장점이 있음.

}
