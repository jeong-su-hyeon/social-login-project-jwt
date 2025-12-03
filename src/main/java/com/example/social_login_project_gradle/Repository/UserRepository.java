package com.example.social_login_project_gradle.Repository;

import com.example.social_login_project_gradle.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// 기본 제공 CRUD 메서드
// save() : 데이터 저장
// findById() : 특정 id로 데이터 조회
// delete() : 데이터 삭제
// findAll() : 전체 데이터 목록 조회 등
// -> 반복적인 CRUD 메서드를 작성하지 않아도 된다는 점에서 큰 장점
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // [조회] username으로 사용자 정보 조회
    UserEntity findByUsername(String username);

    // [조회] username이 존재하는지 여부 확인 (아이디 중복 여부)
    // -> 회원 가입 유효성 검사, 사용자명 중복 체크 API에 사용
    // -> SELECT COUNT(*) FROM user_entity WHERE username = ?;
    Boolean existsByUsername(String username);

    // [조회] username과 password가 일치하는 사용자 조회
    // -> 로그인 검증에 사용
    // -> 실제로 사용은 안 함 !! 위험한 방식이래요
    // -> username과 password를 동시에 비교해서 찾는 방식은 암호화 구조와 맞지 않으며, 보안상 위험함!
    // -> 보통 findByUsername()으로 사용자를 찾고 BCrypt..matches()를 사용해서 암호화된 비밀번호를 비교함
    UserEntity findByUsernameAndPassword(String username, String password);
}
