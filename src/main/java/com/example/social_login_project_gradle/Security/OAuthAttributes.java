package com.example.social_login_project_gradle.Security;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

// 외부 서비스에서 받아온 사용자 정보를 어플리케이션에서 사용할 수 있도록 변환 DTO
@Getter
public class OAuthAttributes {

    // OAuth2 로그인 시 전달받는 사용자 정보
    private Map<String, Object> attributes; // 외부 인증자로부터 받은 사용자 정보 Map
    private String nameAttributeKey;        // 사용자 식별에 사용할 키 (sub, id 등)
    private String name;                    // 사용자 이름
    private String email;                   // 사용자 이메일
    private String picture;                 // 프로필 사진 URL
    private String id;                      // OAuth 사용자 고유 ID (실제 유저 식별)

    // [생성자] 모든 필드를 초기화
    // 빌더 패턴 - 파라미터가 많을 때 유용
    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, String id) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.id = id;
    }

    // OAuth 제공자 구분에 따라 처리할 메서드 (현재는 Google만 처리)
    public static OAuthAttributes of (String registrationId, String userNameAttributeName,Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String)attributes.get("name")) // 사용자 이름
                .email((String)attributes.get("email"))  // 사용자 이메일
                .picture((String)attributes.get("picture")) // 프로필사진
                .id((String)attributes.get(userNameAttributeName)) // 사용자 id (sub, id 등)
                .attributes(attributes)                     // 전체 정보 Map
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

}
