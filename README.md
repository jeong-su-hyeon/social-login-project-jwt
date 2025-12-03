# social-login-project-jwt
Study project on Spring Security &amp; OAuth for social login (JWT)

<br>

## 📂 파일 구성
### `DTO`
- DTO (Data Transfer Object) -> 서버-클라이언트 간에 데이터를 주고 받기 위한 클래스
- `ResponseDTO` :  공통 API 응답 포맷을 제공하는 클래스로, Rest API에서 필수적으로 사용되는 패턴이다.
```json
{
  "data": "asdf",
  "error": null
}
```
### `Repository`
- 데이터베이스에 데이터를 저장, 조회, 수정, 삭제하는 CRUD 작업을 수행한다.
- `UserRepository`
    
    → `findByUsername()` 메서드로 사용자 이름으로 사용자 정보를 조회
    
    → `existsByUsername()` 메서드로 username의 존재 여부를 확인 (아이디 중복 체크용)
    → `findByUsernameAndPassword()` 메서드로 로그인 시, 사용자의 `username` 과 `password`를 비교해 일치 판단
    
- `TodoRepository` : `findByUserId()` 메서드로 사용자 ID에 해당하는 할 일 목록을 조회한다.
### `Service` 
- `UserService` : 회원가입, 로그인 로직을 담당한다. 회원가입 시 클라이언트로부터 받은 `user` 객체의 `username` 을 추출하여 해당 사용자가 이미 존재하는지 중복 체크를 진행하고, 신규 사용자일 경우에만 회원가입 처리를 진행한다. 로그인 시  `usernaem` 으로 사용자 정보를 조회하고, `username` 과 `password` 를 비교하며 서로 일치할 경우에만 `user` 객체를 반환하도록 한다.
- `TodoService` : Todo 객체에 대한 CRUD 작업을 처리한다. 사용자 `id` 로 모든 할 일 리스트를 조회하고, 사용자의 할일 추가, 수정, 삭제 시에도 전체 리스트가 반환되도록 구현하였다.
