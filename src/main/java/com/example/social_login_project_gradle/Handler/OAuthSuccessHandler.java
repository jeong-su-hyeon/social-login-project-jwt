package com.example.social_login_project_gradle.Handler;

import com.example.social_login_project_gradle.Security.TokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.example.social_login_project_gradle.Security.RedirectUrlCookieFilter.REDIRECT_URI_PARAM;

@Slf4j
@AllArgsConstructor // 생성자 자동 생성
@Component
// OAuth2 로그인 성공 시, 처리 로직 핸들러
// 클라이언트가 로그인에 성공하면 JWT 토큰을 생성하고, redirect_url로 토큰을 포함해 리다이렉트
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // [기본 리다이렉트 주소]
    // -> 사용자가 로그인을 시도했지만, redirect_url 정보가 없거나, 시간이 만료되어 쿠키에서 데이터가 삭제되었을 때 사용
    private static final String LOCAL_REDIRECT_URL = "http://localhost:5173";

    // [소셜 로그인 성공 후 실행 메서드]
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException
    {
        // [1] JWT 토큰 생성
        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.create(authentication); // 인증 정보 기반으로 JWT 토큰 생성
        log.info("[TOKEN] {}", token);

        // [2] 쿠키에서 redirect_url 추출
        Optional<Cookie> extractedCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM))
                .findFirst();

        // [3] 최종 리다릭션 경로 생성
        Optional<String> redirectUri = extractedCookie.map(Cookie::getValue);
        log.info("[REDIRECT URI] {}", redirectUri);
        // -> 쿠키 값이 존재하면 해당 값 사용 or 없으면 기본 주소 사용 후, JWT를 쿼리 파라미터로 추가
        String targetUrl = redirectUri.orElseGet(() -> LOCAL_REDIRECT_URL) + "/sociallogin?token=" + token;
        log.info("[TARGET URL] {}", targetUrl);

        // [4] 브라우저 리디렉션
        response.sendRedirect(targetUrl);
    }
}
