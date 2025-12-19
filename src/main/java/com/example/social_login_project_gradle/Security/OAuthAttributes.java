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

    // OAuth 제공자 구분에 따라 처리할 메서드
    public static OAuthAttributes of (String registrationId, String userNameAttributeName,Map<String, Object> attributes) {
        // registrationId : 로그인 플랫폼 식별자 (ex. "google", "naver" 등)
        // userNameAttributesName : OAuth2 프로바이더의 고유 유저 키 (ex. "id")
        // attributes : 실제 사용자 정보가 들어있는 Map

        if("naver".equals(registrationId)) {
            return ofNaver(userNameAttributeName, attributes);
        } else if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        } else if ("github".equals(registrationId)) {
            return ofGithub(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    // [구글 응답 JSON 파싱 메서드]
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

    // [네이버 응답 JSON 파싱 메서드]
    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        // 네이버는 response 안에 데이터가 감싸져 있음
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String)response.get("name")) // 사용자 이름
                .email((String)response.get("email"))   // 사용자 이메일
                .picture((String)response.get("profile_image")) // 프로필사진
                .id((String)response.get(userNameAttributeName)) // 사용자 id (sub, id 등)
                .attributes(attributes)                     // 전체 정보 Map
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // [카카오 응답 JSON 파싱 메서드]
    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {

        //Long id = (Long)attributes.get("id"); // 카카오 사용자 id
        String id = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account"); // Map
        String email = (String)kakaoAccount.get("email"); // 이메일
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String)profile.get("nickname");
        String profileImageUrl = (String)profile.get("profile_image_url");

        return OAuthAttributes.builder()
                .name(nickname) // 사용자 이름
                .email(email)  // 사용자 이메일
                .picture(profileImageUrl) // 프로필사진
                .id("" + id) // 사용자 id 문자열로 변환
                .attributes(attributes)                     // 전체 정보 Map
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // [깃허브 응답 JSON 파싱 메서드]
    public static OAuthAttributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        //Integer id = (Integer)attributes.get("id");
        String id = String.valueOf(attributes.get("id"));

        return OAuthAttributes.builder()
                .name((String)attributes.get("login"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("avatar_url"))
                .id("" + id)
                .attributes(attributes)                     // 전체 정보 Map
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

}
