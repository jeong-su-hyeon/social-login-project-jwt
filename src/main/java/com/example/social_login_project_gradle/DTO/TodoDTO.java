package com.example.social_login_project_gradle.DTO;

import com.example.social_login_project_gradle.Entity.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder                // 빌더 패턴으로 객체 생성
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 포함 생성자
@Data                   // @Getter, @Setter, @ToString 등 자동 생성
// [클라이언트와의 데이터 전송을 위한 Todo DTO 클래스]
public class TodoDTO {

    // [멤버 필드]
    // -> 클라이언트 상에서 필요한 최소한의 정보를 담고 있음
    private Long id;        // 할일 ID (고유)
    private String title;   // 할일 제목
    private boolean done;   // 할일 완료 여부

    // [생성자]
    // -> 서비스 계층에서 Entity 객체를 받아서 클라이언트에게 전송할 DTO로 변환
    public TodoDTO(final TodoEntity entity) {
        this.id = entity.getId();           // 엔터티의 id 복사
        this.title = entity.getTitle();     // 엔터티의 title 복사
        this.done = entity.isDone();        // 엔터티의 done 복사
    }

    // [메서드]
    // DTO 객체 -> 엔터티 객체로 변환 (정적 메서드)
    public static TodoEntity toEntity(final TodoDTO dto) {
        // 빌더 패턴 사용
        return TodoEntity.builder()
                .id(dto.getId())        // DTO의 id 설정
                .title(dto.getTitle())  // DTO의 title 설정
                .done(dto.isDone())     // DTO의 done 여부 설정
                .build();               // 엔터티 인스턴스 반환
    }

    // 클라이언트에서 JSON 형식으로 할 일 추가 요청을 보내면,
    // -> DTO로 받음
    // -> Entity로 변환
    // -> DB에 저장
}
