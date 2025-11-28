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
@Table(name="Todo")
public class TodoEntity {

    @Id                                                 // 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    @Column(name = "id")
    private Long id;        // 할일 ID (고유)

    @Column(name = "user_id")
    private Long userId;    // 사용자 ID

    @Column(name = "title")
    private String title;   // 할일 제목

    @Column(name = "done")
    private boolean done;   // 할일 완료 여부
}
