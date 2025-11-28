package com.example.social_login_project_gradle.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder                // 빌더 패턴으로 객체 생성
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 포함 생성자
@Data                   // @Getter, @Setter, @ToString 등 자동 생성
// [DTO(Data Transfer Object) : 데이터를 주고받을 때 사용하는 객체]
public class UserDTO {

    // [멤버 필드]
    private String token;       // 인증 토큰(JWT), 로그인 후 클라이언트에 전달됨
    private Long id;            // 사용자 id (고유)
    private String username;    // 사용자 이름 (이메일)
    private String password;    // 사용자 패스워드

    // [생성자]
    /**
     * 로그인 요청
     {
        "username": "jsuhyeon7@naver.com",
        "password": "secret"
     }
     * 로그인 응답
     * -> 응답 필드에 password는 반드시 숨기거나 제거
     {
        "id": 1,
        "username": "jsuhyeon7@naver.com",
        "token": "asdfasdfasdfasdf"
     }
    **/
}
