package com.ll.exam.sbb;

import com.ll.exam.sbb.base.RepositoryUtil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AnswerRepository extends JpaRepository<Answer, Integer>, RepositoryUtil {
    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE answer AUTO_INCREMENT = 1", nativeQuery = true)
    /*
    truncate는 jpa와 궁합이 안좋다. 데이터 삭제(delete) 할 때
    auto_increment(ID)를 강제로 1로 지정함으로 truncate와 같은 효과를 낸다.
    */
    void truncate();// 이거 지우면 안됨, truncateTable 하면 자동으로 이게 실행됨
}
