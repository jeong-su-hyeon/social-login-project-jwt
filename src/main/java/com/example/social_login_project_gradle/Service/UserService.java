package com.example.social_login_project_gradle.Service;

import com.example.social_login_project_gradle.Entity.UserEntity;
import com.example.social_login_project_gradle.Repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service                    // 서비스임을 명시
@Slf4j                      // 로그 출력용
@RequiredArgsConstructor    // final 필드 생성자 자동 생성
// 사용자 등록 및 인증을 처리하는 서비스 클래스
public class UserService {
    private final UserRepository userRepository;    // 데이터베이스 조회/저장

    // [추가] 사용자 정보 저장 (회원가입)
    public UserEntity insertUser(UserEntity user) {
        // null 체크
        if (user == null || user.getUsername() == null) {
            throw new RuntimeException("[Service] 유효하지 않은 속성");
        }

        // userEntity의 사용자 이메일 받아오기
        final String username = user.getUsername();

        // 사용자 이메일 중복 체크
        if (userRepository.existsByUsername(username)) {
            log.warn("[Service] 이미 존재하는 사용자 이메일입니다.");
            throw new RuntimeException("[Service] 이미 존재하는 사용자 이메일입니다.");
        }

        // 사용자 정보 저장
        return userRepository.save(user);
    }

    // [조회] 사용자 인증 (로그인)
    // username, password 비교
    public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
        // 1) 사용자 조회 (username 기준)
        final UserEntity user = userRepository.findByUsername(username);

        // 2) 사용자 username, password 일치 여부 판단
        if (user != null && encoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }
}
