package com.example.social_login_project_gradle.Security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

// Spring Security에서 JWT 기반 인증을 처리 (커스텀 필터)
// HTTP 요청마다 실행 됨 -> 요청에 포함된 JWT를 검증, SecurityContext에 인증 정보를 설정
// -> 클라이언트로부터 요청이 들어올 때마다 JWT 토큰을 확인하고, 사용자 인증 정보를 설정하는 필터 역할 !!
@Slf4j
@Service
@RequiredArgsConstructor
@Component // 빈으로 등록
// OncePerRequestFilter : Spring Security에서 제공하는 필터 클래스 (HTTP 요청 당 한 번만 실행되는 필터 생성)
// 검증 완료 후, 유효한 정보면 인증 정보를 SecurityContext에 등록하는 역할
// (로그인 없이 토큰만으로 인증 가능한 구조)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // [토큰 검증 및 사용자 ID 추출 의존성]
    // JWT 토큰 생성, 검증
    private final TokenProvider tokenProvider;

    // [요청 건너뛰기] 특정 요청은 필터를 건너뛰도록 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // OPTIONS 요청은 필터를 건너뛰도록 설정 (브라우저에서 발생하는 CORS 사전 요청 등 무시)
        // -> 클라이언트가 실제 요청 전에 OPTIONS 요청을 보내고, 서버가 이 요청을 허용할지 확인하는 과정
        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }
        return false;
    }

    // [필터 내부 로직] "신뢰할 수 있는가?"를 판단
    // 사용자 요청 -> JWT 파싱 -> 인증 객체 생성 -> SecurityContext 설정
    // 1) 요청 헤더에서 JWT 토큰 추출
    // 2) 추출한 토큰의 유효성 검증
    // 3) 유효한 경우, Spring Security의 인증 객체 생성
    // 4) 인증 정보를 SecurityContext에 설정
    // 5) 필터로 요청 넘김
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        try {
            // 1) 요청 헤더에서 JWT 토큰 추출, 파싱
            String token = parseBearerToken(request);
            log.info("[JwtAuthenticationFilte] Starting internal filter logic");

            // 토큰이 존재하고, null이 아닌 경우
            if (token != null && StringUtils.hasText(token)) {
                // 2) 토큰 검증 및 userId 추출
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("[JwtAuthenticationFilte] Authenticated userId: " + userId);

                // 3) 인증 객체 생성 (userId 기반)
                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, AuthorityUtils.NO_AUTHORITIES);  // 권한 없음

                // 4) 인증 객체에 상세 정보 추가
                // 요청에 대한 세부 정보(IP, 세션 등)를 추가함 (감사 로그, 세션 추적에 사용함)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5) SecurityContext에 등록
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext(); // 새로운 SecurityContext 생성
                securityContext.setAuthentication(authentication); // 인증 객체 설정
                SecurityContextHolder.setContext(securityContext); // 현재 스레드에 SecurityContext 등록
                // -> 나중에 @AuthenticaitonPrincipal이나, SecurityContextHolder.getContext()를 통해 현재 로그인한 사용자 정보에 접근 가능함
            }
        } catch (Exception ex) {
            log.error("[JwtAuthenticationFilte] Could not set user authentication in security context.");
        }

        // 6) 다음 필터로 요청 전달 (JWT 토큰 검증 완료 후)
        filterChain.doFilter(request, response);
    }

    // 클라이언트 요청에서 JWT 토큰을 꺼내옴
    // 클라이언트의 요청에서 Authorization 헤더에 담긴 토큰을 추출하는 역할
    // 요청 헤더 예시) Authorization: Bearer eyasldfkjopiwjc...
    private String parseBearerToken(HttpServletRequest request) {
        // 1) 요청 헤더의 "Authorization" 가져오기
        String bearerToken = request.getHeader("Authorization");
        // 2) "Bearer " 이후의 문자열만 추출
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null; // -> 헤더가 아예 없거나 형식이 잘못된 경우 null 반환 (검증 대상이 아님)
    }

}
