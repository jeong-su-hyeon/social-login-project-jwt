package com.example.social_login_project_gradle.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder            // 객체 생성 시 Builder 패턴을 사용할 수 있도록 함
@NoArgsConstructor  // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함한 생성자 자동 생성
@Data               // @Getter, @Setter, @ToString 등을 자동 생성
@Entity             // JPA 엔터티임을 명시
@Table(name = "User")
public class UserEntity {

    @Id                                                     // 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // 자동 증가
    @Column(name = "id")
    private Long id;            // 사용자 ID (고유)

    @Column(nullable = false, unique = true, name = "username")
    private String username;    // 사용자 이름 (이메일)

    @Column(name = "password")
    private String password;    // 사용자 패스워드 (소셜 로그인 시, null)

    @Column(name = "role")
    private String role;        // 사용자 역할 (ex. ROLE_USER, ROLE_ADMIN)

    @Column(name = "auth_provider")
    private String authProvider;
}
