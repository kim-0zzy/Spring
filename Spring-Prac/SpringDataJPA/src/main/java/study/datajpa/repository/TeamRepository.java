package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Team;
//                                                     ↓ <엔티티명, Pk의 Type>
public interface TeamRepository extends JpaRepository<Team, Long> {
}
