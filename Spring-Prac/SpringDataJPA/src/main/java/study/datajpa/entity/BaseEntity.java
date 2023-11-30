package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


// S-DataJpa의 Auditing 사용 방법.
// 강의는 이렇게 했지만 BaseTimeEntity 클래스에 등록일,수정일을 넣고
// 등록자, 수정자는 BaseEntity 클래스에 BaseTimeEntity 클래스를 상속받아 사용하는 것을 권장함.
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

    // 등록일, 수정일
    // 이거 사용하려면 DataJpaApplication에 @EnableJpaAuditing 어노테이션 넣어야함
    // 만약 등록일만 필요하다 싶으면 별도 클래스 생성 안하고 엔티티에 ##만 넣으면 됨.
    /* ## */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate; /* ## */
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // 등록자, 수정자
    // 이거 사용하려면 DataJpaApplication에 AuditorAware 메서드 구현해야함.
    @CreatedBy
    @Column(updatable = false)
    private String createBy;

    @LastModifiedBy
    private String lastModifiedBy;

}
