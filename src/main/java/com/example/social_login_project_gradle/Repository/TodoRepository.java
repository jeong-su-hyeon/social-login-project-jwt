package com.example.social_login_project_gradle.Repository;

import com.example.social_login_project_gradle.Entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// TodoEntity를 관리하는 JPA Repository 인터페이스
// JPA : 별도의 쿼리문을 작성하지 않아도 TodoEntity의 CRUD 작업을 수행할 수 있음
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    // 사용자 ID의 모든 Todo 목록 조회
    // 메서드 명으로 쿼리 자동 생성
    // -> SELECT * FROM todo WHERE user_id = ?
    List<TodoEntity> findByUserId(Long userId);

    // JPQL을 사용하여 userId로 단일 TodoEntity를 조회하는 커스텀 쿼리'
    // 직접 정의한 JPQL 쿼리 실행
    // 테이블명이 아닌, 엔티티 클래스명, 필드명을 사용
    // ?1 : 첫번째 파라미터를 의미
    // -> User에 해당하는 할 일 하나를 조회하여 TodoEntity를 반환함
    // -> (주의) 결과가 하나만 있을 경우에만 사용함
    @Query("SELECT t FROM TodoEntity t WEHRE t.userId = ?1 userId")
    TodoEntity findByUserIdQuery(Long userId);
}
