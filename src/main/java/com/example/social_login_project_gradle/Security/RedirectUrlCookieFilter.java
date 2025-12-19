package com.example.social_login_project_gradle.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
// Spring Security에서 소셜 로그인할 때, 클라이언트가 요청한 리디렉션 URL을 쿠키에 저장하는 용도
// 소셜 로그인 요청 시, 클라이언트가 보낸 redirect_url 파라미터를 쿠키로 저장해놓고
// 로그인 완료 후 해당 URL로 리다이렉트하기 위한 기반을 마련
public class RedirectUrlCookieFilter extends OncePerRequestFilter {

    // OncePerRequestFilter : 요청 당 한 번만 실행되는 커스텀 필터 클래스

    public static final String REDIRECT_URI_PARAM = "redirect_url"; // 요청 파라미터 및 쿠키 이름
    private static final int MAX_AGE=  180; // 쿠키 유효 시간 180초


    // 클라이언트로부터 요청이 들어올 때마다 한 번씩 실행되는 메서드
    // (소셜 로그인만 처리)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        // [1] 요청 - 소셜 로그인 여부 확인
        if(request.getRequestURI().startsWith("/oauth2/authorizqation")) {
            try {
                log.info("[REQUST URI] {}", request.getRequestURI()); // 디버깅

                // [2] redirect_url 파라미터 가져오기
                String redirectUrl = request.getParameter(REDIRECT_URI_PARAM);

                // [3] 쿠키 생성 및 설정
                // 쿠키 - 로그인 성공 후 사용자에게 돌려보낼 URL
                Cookie cookie = new Cookie(REDIRECT_URI_PARAM, redirectUrl);
                cookie.setPath("/");       // 전체 경로에서 쿠키 사용 가능하도록 설정
                cookie.setHttpOnly(true);  // JS에서 접근 불가능하도록 설정 (보안 강화)
                cookie.setMaxAge(MAX_AGE); // 쿠키 만료 시간
            } catch (Exception ex) {
                log.error("Could not set user authentication in security context", ex);
                log.info("Unauthorized request");
            }
        }

        // [4] 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}
