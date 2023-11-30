package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.hql.spi.id.persistent.PersistentTableBulkIdStrategy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.EntityGraph;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;


/*
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)      일반적인 @GeneratedValue 사용 상황.
    public class Item {

    @Id @GeneratedValue
    private String id;

    public Item(String id) {
        this.id = id;

    }
}*/
                                                    // ex) JPA의 식별자 생성 전략이 @GeneratedValue를 사용하지 않고  ID를 직접 할당할 때.
@Entity                                             // 테이블 분할 등 모종의 이유로 @GeneratedValue를 사용할 수 없을 때
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id @GeneratedValue
    private String id;

    public Item(String id) {
        this.id = id;
    }
    @CreatedDate
    private LocalDateTime createdDate;
    @Override
    public String getId() {
        return null;
    }
    @Override   // isNew를 사용하면 S-Datajpa의 save기능을 사용할 때 merge가 호출되는 것을 막을 수 있다.
    public boolean isNew() {
        return createdDate == null;
    }
}