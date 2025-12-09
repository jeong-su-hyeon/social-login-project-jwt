package com.example.social_login_project_gradle.Controller;

import com.example.social_login_project_gradle.DTO.ResponseDTO;
import com.example.social_login_project_gradle.DTO.TodoDTO;
import com.example.social_login_project_gradle.Entity.TodoEntity;
import com.example.social_login_project_gradle.Service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor // 생성자 주입
@RestController          // Rest 컨트롤러 선언 (@Controller + @ResponseBody)
@RequestMapping("todo")  // prefix
// 사용자의 할일 목록을 CRUD 처리하는 컨트롤러
public class TodoController {
    private final TodoService todoService;

    // [생성] 할 일 생성 API
    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        // @AuthenticationPrincipal -> JWT에서 추출한 userId 가져옴

        try {
            // 1) DTO -> Entity로 변환 (DB 저장을 위해)
            TodoEntity todoEntity = TodoDTO.toEntity(dto);
            // 2) Entity 설정
            todoEntity.setId(null); // -> 새 Entity의 id null 처리
            todoEntity.setUserId(Long.parseLong(userId)); // -> 인증된 userId 설정
            // 3) 서비스 호출 (Todo 생성)
            List<TodoEntity> entities = todoService.createTodoList(todoEntity);
            // 4) Entity -> DTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            // 5) 정상 응답 객체 생성
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO> builder().data(dtos).build();

            return ResponseEntity.ok().body(response); // [200 OK 응답]
        } catch (Exception ex) {
            String error = ex.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO> builder().error(error).build(); // 에러 응답 객체 생성
            return ResponseEntity.badRequest().body(response); // [400 Bad Request 응답]
        }
    }

    // [조회] 할 일 목록 조회 API
    @GetMapping
    public ResponseEntity<?> selectTodoList(@AuthenticationPrincipal String userId) {
        // 1) 서비스 호출 (Todo 조회)
        List<TodoEntity> entities = todoService.selectTodoList(Long.parseLong(userId));
        // 2) 조회 결과 -> DTO로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        // 3) 정상 응답 객체 생성
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO> builder().data(dtos).build();

        return ResponseEntity.ok().body(response); // [200 OK 응답]
    }

    // [수정] 할 일 수정 API
    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        // 1) DTO -> Entity로 변환 (DB 저장을 위해)
        TodoEntity todoEntity = TodoDTO.toEntity(dto);
        // 2) Entity 설정
        todoEntity.setUserId(Long.parseLong(userId)); // 수정 요청을 보낸 userId를 세팅 (왜???)
        // 3) 서비스 호출 (Todo 수정)
        List<TodoEntity> entities = todoService.updateTodoList(todoEntity);
        // 4) Entity -> DTO 리스트로 변환 (수정된 목록을 다시 반환함)
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList()); // 리스트 반환
        // 5) 정상 응답 객체 생성
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO> builder().data(dtos).build();

        return ResponseEntity.ok().body(response); // [200 OK 응답]
    }

    // [삭제] 할 일 삭제 API
    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        // @AuthenticationPrincipal -> JWT에서 추출한 userId 가져옴

        try {
            // 1) DTO -> Entity로 변환 (DB 저장을 위해)
            TodoEntity todoEntity = TodoDTO.toEntity(dto);
            // 2) Entity 설정
            todoEntity.setUserId(Long.parseLong(userId)); // 수정 요청을 보낸 userId를 세팅 (왜???)
            // 3) 서비스 호출 (Todo 삭제)
            List<TodoEntity> entities = todoService.deleteTodoList(todoEntity);
            // 4) Entity -> DTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            // 5) 정상 응답 객체 생성
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO> builder().data(dtos).build();

            return ResponseEntity.ok().body(response); // [200 OK 응답]
        } catch (Exception ex) {
            String error = ex.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO> builder().error(error).build(); // 에러 응답 객체 생성
            return ResponseEntity.badRequest().body(response); // [400 Bad Request 응답]
        }
    }
}
