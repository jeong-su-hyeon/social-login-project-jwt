package com.example.social_login_project_gradle.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder                // 빌더 패턴으로 객체 생성
@NoArgsConstructor      // 기본 생성자
@AllArgsConstructor     // 모든 필드 포함 생성자
@Data                   // @Getter, @Setter, @ToString 등 자동 생성
// [API 응답 데이터를 감싸는 제네릭 클래스]
// -> 공통 API 응답 포맷을 제공
// -> Rest API에서 필수적으로 사용되는 패턴
public class ResponseDTO<T> {

    // <T> : UserDTO 타입이든 TodoDTO 타입이든 <T> 안에 담을 수 있다.
    // -> ResponseDTO<UserDTO> or ResponseDTO<TodoDTO>

    // [멤버 필드]
    private String error;    // 에러 메시지 (ex. "Unauthorized", "Validation failed")
    private List<T> data;   // 실제 응답 데이터 리스트 (TodoDTO, UserDTO 타입 등)

    /**
     * 성공 응답
     {
        "data": [
            {
                "id": 1,
                "title": "할일1",
                "done": false
            },
             {
                 "id": 2,
                 "title": "할일2",
                 "done": true
             }
        ],
        "error": null
     }
     * 실패 응답
     {
        "data": null,
        "error": "사용자를 찾을 수 없습니다."
     }
     **/
}
