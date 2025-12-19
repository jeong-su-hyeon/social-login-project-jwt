package com.example.social_login_project_gradle.Security;

import com.example.social_login_project_gradle.Entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
// JWT 토큰을 생성하고 검증하는 역할을 수행
// 1) JWT 토큰 생성 : 사용자 로그인 성공 시, 사용자 정보 기반으로 JWT를 생성해서 클라이언트에게 전달
// 2) JWT 토큰 검증 및 사용자 ID 추출 : 클라이언트가 API 요청 시 전달한 토큰을 검증, 사용자 ID를 추출해 인증에 활용할 수 있도록 도와줌
public class TokenProvider {

    // 비밀 키 (JWT 서명할 때 사용하는 비밀 문자열)
    // -> 토큰의 무결성 보장
    private static final String SECRET_KEY = "FWFQA3UbMjQu4NkmIfM6rbrmVK0D4RQjsksGZimBFavSGUveAXWWLf7FRSyjhHtMV9M1iOcegkqy464AdwrKX15EGCm7UB6lFftPTIDj";

    // 비밀 키를 바탕으로 만들어진 서명용 키 객체 (HMAC SHA)
    // 비밀 키를 바이트 배열로 변환한 뒤, hmacShaKeyFor() 메서드를 사용해
    // HMAC SHA-512 알고리즘을 사용하는 서명용 Key 객체로 변환
    // 토큰을 사용할 땐 서명용 키, 토큰을 검증할 땐 검증용 키로 사용됨
    // -> JWT의 위,변조를 막고, 유효한 토큰인지 확인할 수 있음
    //private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    private static final SecretKey SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // [생성] (일반 로그인 전용) 사용자 정보 기반 JWT 토큰 생성
    public String create(UserEntity userEntity) {
        // 1) 토큰 만료 시간을 현재 시각으로부터 1일 뒤로 설정
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        // 2) JWT 생성 및 반환
        return Jwts.builder()
                //.signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
                .signWith(SIGNING_KEY)                         // 서명 알고리즘과 키 설정
                .subject(String.valueOf(userEntity.getId()))  // 사용자 ID를 subject(주체)로 설정
                .issuer("social-login-project-jwt")           // 토큰 발급자 정보 설정
                .issuedAt(new Date())                         // 토큰 발급 시각 설정
                .expiration(expiryDate)                       // 토큰 만료 시간 설정
                .compact();                                      // 토큰 생성 완료
    }

    // [생성] (소셜 로그인 전용) 사용자 정보 기반 JWT 토큰 생성
    public String create(final Authentication authentication) {
        CustomUser userPrincipal = (CustomUser) authentication.getPrincipal();

        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        return Jwts.builder()
                .signWith(SIGNING_KEY)
                .subject(userPrincipal.getName())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .compact();
    }

    // [검증] JWT 토큰을 검증하고, 포함된 사용자 id를 반환
    // 클라이언트로부터 전달받은 JWT 토큰을 분석해 그 안에 담겨있는 사용자 정보를 확인
    // -> userId가 문자열 형태로 반환 됨
    public String validateAndGetUserId(String token) {
        // 1) 토큰 파싱 및 검증 (서명이 유효한지 확인)
        // parserBuilder() -> parser()로 변경
        Claims claims = Jwts.parser()
                .verifyWith(SIGNING_KEY) // 서명 키 설정
                .build()
                .parseSignedClaims(token)      // 토큰 파싱
                .getPayload();                 // Payload(Claims) 추출

        return  claims.getSubject();        // 사용자 ID (subject) 반환
    }

    // [생성] 사용자 ID만을 기반으로 토큰 생성 (예. OAuth2 사용자 등)
    // creat과 비슷한 역할
    // 차이점) USerEntity 객체 전체를 받는 대신, 단순히 userId 하나만 받아서 JWT를 생성
    // -> 사용자 userId만으로 토큰을 생성할 때 사용하는 메서드 (간단, 효율적)
    // -> 로그인 이후, 사용자 정보를 간단히 전달할 때 사용
    public String createByUserId(final Long userId) {
        // 1) 토큰 만료 시간을 현재 시각으로부터 1일 뒤로 설정
        Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));

        // 2) JWT 토큰 생성
        return Jwts.builder()
                .signWith(SIGNING_KEY)           // 서명 (토큰 무결성 보장)
                .subject(String.valueOf(userId)) // 사용자 ID 설정
                .issuedAt(new Date())            // 토큰 발급 시각 설정
                .expiration(expiryDate)          // 토큰 만료 시각 설정
                .compact(); // 최종 토큰 문자열 생성
    }
}
