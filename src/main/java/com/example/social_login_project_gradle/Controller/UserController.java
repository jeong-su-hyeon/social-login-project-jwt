package com.example.social_login_project_gradle.Controller;

import com.example.social_login_project_gradle.DTO.ResponseDTO;
import com.example.social_login_project_gradle.DTO.UserDTO;
import com.example.social_login_project_gradle.Entity.UserEntity;
import com.example.social_login_project_gradle.Security.TokenProvider;
import com.example.social_login_project_gradle.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor // 생성자 주입
@RestController          // Rest 컨트롤러 선언 (@Controller + @ResponseBody)
@RequestMapping("/auth") // prefix
// 사용자 회원가입, 로그인 처리, JWT 발급 담당 컨트롤러
public class UserController {

    private final UserService userService;      // User 관련 서비스 로직 (데이터베이스 연동)
    private final TokenProvider tokenProvider;  // JWT 토큰 생성
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 패스워드 암호화

    // [회원가입]
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDTO userDTO) {
        try {
            // 1) [유효성 검사] 비밀번호가 없으면 예외 발생
            if (userDTO == null || userDTO.getPassword() == null) {
                throw new RuntimeException("[UserController] 유효하지 않은 비밀번호입니다.");
            }

            // 2) 사용자 객체 생성, 비번 암호화
            UserEntity user = UserEntity.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();

            // 3) DB에 사용자 정보 저장
            UserEntity registeredUser = userService.signupUser(user);

            // 4) 응답 객체 생성 (비밀번호 제외)
            // -> 왜 ResponseDTO .data()로 감싸지 않고 UserDTO로 반환하는지 ... 의문
            UserDTO responseUserDTO = UserDTO.builder()
                    .id(registeredUser.getId())
                    .username(registeredUser.getUsername())
                    .build();

            // 5) 200 OK 응답 반환
            return ResponseEntity.ok().body(responseUserDTO);

        } catch (Exception ex) {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error(ex.getMessage())
                    .build();

            // 400
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // [로그인]
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserDTO userDTO) {
        // 1) 사용자 인증 시도 (유효성 검사)
        UserEntity user = userService.getByCredentials(
                userDTO.getUsername(), userDTO.getPassword(), passwordEncoder
        );

        // 인증 성공 시
        if (user != null) {
            // 2) JWT 토큰 발급
            final String token = tokenProvider.create(user);

            // 3) DTO에 사용자 정보, 토큰 포함
            final UserDTO responseUserDTO = UserDTO.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        }

        // 인증 실패 시
        else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("[UserController] 로그인 실패")
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
