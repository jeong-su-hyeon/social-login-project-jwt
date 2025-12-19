package com.example.social_login_project_gradle.Security;

import com.example.social_login_project_gradle.Entity.UserEntity;
import com.example.social_login_project_gradle.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
// 소셜 로그인 성공 후, 사용자 정보를 가공해 CustomUser 객체로 반환
// OAuth2UserService 인터페이스 : Spring Security가 소셜 로그인 처리 중 OAuth2 서버로부터 사용자 정보를 받아왔을 때 호출
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // @Autowired : 스프링 앱 실행 시, UserRepository를 찾아 자동 주입
    // @RequiredArgsConstructor 활용하는 방식을 권장하지만,
    // 필드에서 간단하게 쓸 수 있음
    @Autowired
    UserRepository userRepository; // 사용자 DB

    // [loadUser 메서드] 사용자 정보를 받아와 가공 후 최종적으로 반환
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[DEBUG] loadUser");

        // [1] 사용자 정보 로딩
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest); // 사용자 정보 조회

        // [2] OAuth 공급자 이름 추출 (ex. google, naver, github 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // [3] 사용자 정보 매핑
        String usernameAttributename = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        log.info("[DEBUG] registrationID = " + registrationId);
        log.info("[DEBUG] userNameAttributeName = " + usernameAttributename);

        // 공급자로부터 받은 사용자 정보를 OAuthattributes DTO로 매핑
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, usernameAttributename,oauth2User.getAttributes());

        // [4] 사용자 정보 추출 (속성 꺼내기, 기본값 처리)
        String nameAttributeKey = attributes.getNameAttributeKey();
        String name = attributes.getName();
        String email = attributes.getEmail();
        String picture = attributes.getPicture();
        String id = attributes.getId();
        String socialType = registrationId;

        // [깃허브 이메일 요청]
        if (email == null) {
            log.info("[DEBUG] email is null");
            // -> Github API에 접근할 수 있는 OAuth 인증 토큰
            // 이 토큰으로 이메일 목록을 받아오고, 그 중에서 기본(primary) 이메일을 꺼내서 저장
            email = getEmailFromGithub(userRequest.getAccessToken().getTokenValue());
            log.info("[DEBUG] Github email = " + email); // 이메일 확인
        }

        // 디버깅
        log.info("[DEBUG] nameAttributeKey = " + nameAttributeKey);
        log.info("[DEBUG] id = " + id);
        log.info("[DEBUG] socialType = " + socialType);
        log.info("[DEBUG] name = " + name);
        log.info("[DEBUG] email = " + email);
        log.info("[DEBUG] picture = " + picture);
        log.info("[DEBUG] attributes = " + attributes);

        // NULL 방지 처리
        if(name == null) name = "";
        if(email == null) email = "";

        // [5] 권한 설정
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(); // 1) 권한 목록 생성
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER"); // 2) 권한 부여
        authorities.add(authority); // 3) 권한 추가

        String username = email;    // 사용자명 => 이메일
        String authProvider = socialType; // OAuth 제공자 정보

        UserEntity userEntity = null;

        // [6] 사용자 등록/조회
        // DB에 사용자 정보가 없다면 => 등록
        if (!userRepository.existsByUsername(username)) {
            userEntity = UserEntity.builder()
                    .username(username)
                    .authProvider(authProvider)
                    .build();
            userEntity = userRepository.save(userEntity); // DB에 사용자 정보 저장
        }
        // DB에 사용자 정보가 있다면 => 조회
        else {
            userEntity = userRepository.findByUsername(username); // 기존 사용자 조회
        }

        // [7] 사용자 정보 반환
        // -> 이후 인증된 사용자 정보로 사용
        return new CustomUser(userEntity.getId(), email, name, authorities, attributes);
    }

    private String getEmailFromGithub(String accessToken) {
        // Github에서는 기본 응답만으로는 부족하기 때문에 accessToken으로 한 번 더 API를 호출해
        // 필요한 정보를 보완하는 작업이 필요하다.

        // 깃허브의 이메일을 조회하는 공식 API 엔트포인트
        String url = "https://api.github.com/user/emails";
        // Spring에서 외부로 HTTP 요청하기 위한 객체
        RestTemplate restTemplate = new RestTemplate();
        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // accessToken 설정
        headers.set("Accept", "application/vnd.github.v3+json"); // Github API 버전 명시
        // 헤더만 포함된 GET 요청 본문
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 이메일 정보 요청 (RestTemplate)
        // Github 이메일 API 호출 (응답 - 이메일 List)
        ResponseEntity<List> response = restTemplate.exchange(
                                                        url,
                                                        HttpMethod.GET,
                                                        entity,
                                                        List.class);

        List<Map<String, Object>> emails = response.getBody(); // 응답에서 이메일 List 추출

        // 주요 이메일(primary email) 추출
        if(emails != null) {
            for(Map<String, Object> email : emails) {
                if((Boolean) email.get("primary")) {
                    return (String) email.get("email");
                }
            }
        }

        return null;
    }


}
