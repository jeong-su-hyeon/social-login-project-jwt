package com.example.social_login_project_gradle.Security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;

// 소셜 로그인한 사용자 정보를 담는 클래스
// -> 로그인 성공 후, 인증된 사용자 객체로 사용
// Spring Security의 DefaultOAuth2User를 확장해 사용자 ID, 이메일, 사용자명을 추가로 보관
public class CustomUser extends DefaultOAuth2User {

    private static final long serialVersionUID = 1L; // 직렬화 UID

    // -> 자주 사용하는 값들만 따로 필드로 꺼내 관리
    private Long id;        // 사용자 고유 id
    private String email;   // 사용자 이메일
    private String username; // 사용자 이름 또는 닉네임

    // [생성자] 인증된 사용자 정보를 담는 생성자
    public CustomUser(
            Long id,         // 사용자 id
            String email,    // 이메일
            String username, // 이름
            Collection<? extends GrantedAuthority> authorities, // 권한 목록
            OAuthAttributes attributes  // OAuth 사용자 정보 DTO
    ) {
        // 1) 부모 클래스 생성자 호출
        // 사용자 권한, 사용자 정보, 고유 식별 키를 전달
        // -> attributes.getAttributes() : 속성 맵
        // -> attributes.getNameAttributeKey() : 고유 식별 키
        super(authorities, attributes.getAttributes(), attributes.getNameAttributeKey());

        // 2) 추가 필드 초기화
        // -> 추가적인 데이터 처리 시 직접 사용하게 될 값
        this.id = id;
        this.email = email;
        this.username = username;
    }

    // [Getter] 사용자 이메일 반환
    // -> 서비스나 API 응답에서 사용자 정보를 사용할 때 활용
    public String getEmail() {
        return email;
    }

    // [Getter] 사용자 이름 반환
    public String getUsername() {
        return username;
    }

    // 필수 구현!
    // [Getter] 사용자 고유 id를 문자열로 반환
    // -> Spring Security에서 인증된 사용자를 식별할 때 활용
    @Override
    public String getName() {
        return id + "";
    }
}

