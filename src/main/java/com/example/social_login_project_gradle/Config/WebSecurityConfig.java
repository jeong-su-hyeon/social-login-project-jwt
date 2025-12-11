package com.example.social_login_project_gradle.Config;

import com.example.social_login_project_gradle.Security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration      // 설정 클래스로 명시
@EnableWebSecurity  // Spring Security 활성화
// JWT 기반 인증 적용, 세션 사용 x, CORS 설정까지 포함
public class WebSecurityConfig {

    // [JWT 인증 필터 의존성]
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        // 생성자 주입 방식
        // Spring Security 필터 체인에 커스텀 필터 클래스 주입
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // [보안 필터 체인 설정]
    // (인증, 인가, 세션, 예외 처리, JWT 필터, 로그인 처리 등 설정)
    @Bean   // 생성된 SecurityFilterChain 객체를 Spring Container가 자동으로 관리
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // 1) CORS 설정 활성화 (ex. 프론트엔드와 백엔드가 서로 다른 도메인에 있을 때, 교차 출처 요청(CORS)할 수 있게 됨)
                .csrf(csrf -> csrf.disable()) // 2) CSRF 보호 비활성화 (REST API 서버에서 주로 사용, 세션 쿠키 방식이 아니기 때문)                        .httpBasic(httpBasic -> httpBasic.disable())
                .httpBasic(httpBasic -> httpBasic.disable()) // 3) httpBasic 비활성화 (id와 비밀번호를 매 요청마다 헤더에 실어서 보내는 방식) (-> JWT 기반 인증)
                .sessionManagement(session -> session // 4) 기본 인증 방식 비활성화
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함 (JWT 기반 인증)
                )
                // 5) 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/auth/**").permitAll() // 루트 및 /auth/** 경로는 인증 없이 사용 가능
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                // 6) JWT 인증 필터 등록
                // -> UsernamepasswordAuthenticaitonFilter 이후에 실행되도록
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                // 7) 인증 실패 시 응답 (403 반환)
                .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                );

        return http.build();
    }

    // [CORS 설정 정의]
    // -> 백엔드 서버에 어떠한 프론트엔드에서 접근할 수 있을 지, 어떤 요청을 허용할지 등 CORS 정책 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 허용할 프론트엔드 도메인
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS")); // 허용할 요청 메서드
        configuration.setAllowedHeaders(List.of("*")); // 모든 요청 헤더 허용
        configuration.setExposedHeaders(List.of("*")); // 응답 헤더 노출 (응답 시 브라우저에서 접근할 수 있도록 허용할 헤드)

        // 위 설정을 모든 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로(요청)에 대해 위 CORS 설정 적용

        return source;
    }
}
