package study.datajpa.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;


//Jpa의 Auditing 사용법
@MappedSuperclass // 이걸 사용하면 상속 받는 클래스가 이 클래스를 진짜 상속받는게 아니라 클래스의 데이터만 상속받아서 사용할 수 있음.
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updateDate = now;
        // 업데이트 날짜는 null로 비워놔야하는거 아님?
        // ㄴㄴ. 업데이트 날짜에도 데이터를 넣어놓는게 나중에 쿼리생성할때도 편함.
    }

    @PreUpdate
    public void preUpdate(){
        updateDate = LocalDateTime.now();
    }

}
