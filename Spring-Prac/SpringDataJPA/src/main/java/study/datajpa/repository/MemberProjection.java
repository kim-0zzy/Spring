package study.datajpa.repository;


// MemberRepository - findByNativeProjection 확인
public interface MemberProjection {

    Long getId();
    String getUsername();
    String getTeamName();
}
