package com.example.social_login_project_gradle.Service;

import com.example.social_login_project_gradle.Entity.TodoEntity;
import com.example.social_login_project_gradle.Repository.TodoRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service                    // 서비스임을 명시
@Slf4j                      // 로그 출력용
@RequiredArgsConstructor    // final 필드 생성자 자동 생성
// 할일 등록 및 인증을 처리하는 서비스 클래스
public class TodoService {

    private final TodoRepository todoRepository;

    // [유효성 검사]
    private void validate(TodoEntity todoEntity) {
        // null 체크
        if(todoEntity == null) {
            log.warn("[Service] TodoEntity가 존재하지 않습니다.");
            throw new RuntimeException("[Service] TodoEntity는 null이 존재하면 안됩니다.");
        }
    }

    // [조회] 사용자 ID로 모든 할일 리스트 조회
    public List<TodoEntity> selectTodoList(final Long userId) {
        return todoRepository.findByUserId(userId);
    }

    // [추가, 조회] 사용자 할일 추가 -> 전체 할일 리스트 조회
    public List<TodoEntity> createTodoList(final TodoEntity todoEntity) {
        // (호출) 유효성 검사
        validate(todoEntity);

        // todoEntity를 DB에 저장
        todoRepository.save(todoEntity);
        log.info("[Service] TodoEntity Id {{}} 저장 완료", todoEntity.getId());

        // 사용자 ID로 할일 목록 조회 및 반환 (화면 갱신 처리)
        return selectTodoList(todoEntity.getUserId());
    }

    // [수정, 조회] 할일 항목 수정 -> todoList
    public List<TodoEntity> updateTodoList(final TodoEntity todoEntity) {
        // (호출) 유효성 검사
        validate(todoEntity);

        // DB에서 todoId 조회
        final Optional<TodoEntity> original = todoRepository.findById(todoEntity.getId());

        // userId가 존재하면 데이터 수정
        original.ifPresent(todo -> {
            todo.setTitle(todoEntity.getTitle());   // -> 제목 수정
            todo.setDone(todoEntity.isDone());      // -> 완료 여부 수정
            todoRepository.save(todoEntity);        // 수정된 데이터 저장
        });

        // 사용자 ID로 할일 목록 조회 및 반환 (화면 갱신 처리)
        return selectTodoList(todoEntity.getUserId());
    }

    // [삭제, 조회]
    public List<TodoEntity> deleteTodoList(final TodoEntity todoEntity) {
        // (호출) 유효성 검사
        validate(todoEntity);

        // todoEntity를 DB에서 삭제
        try {
            todoRepository.delete(todoEntity);
        } catch (Exception ex) {
            log.error("[Service] TodoEntity 삭제 중 에러 발생 Id{{}}", todoEntity.getId(), ex);
            throw new RuntimeException("[Service] TodoEntity 삭제 중 에러 발생 " + todoEntity.getId());
        }

        // 사용자 ID로 할일 목록 조회 및 반환 (화면 갱신 처리)
        return selectTodoList(todoEntity.getUserId());
    }

}
