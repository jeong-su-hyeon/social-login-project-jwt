package com.example.social_login_project_gradle.Controller;

import com.example.social_login_project_gradle.DTO.UserDTO;
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
// 사용자 회원가입, 로그인 처리 담당 컨트롤러
public class UserController {

    private final UserService userService;      // User 관련 서비스 로직
    private final TokenProvider tokenProvider;  // JWT 토큰 생성
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 비밀번호 암호화

    // [회원가입]
//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
//        try {
//            // 비밀번호가 없으면 예외 발생
//            if (userDTO == null || userDTO.getPassword() == null) {
//
//            }
//        } catch (Exception ex) {
//
//        }
//    }

    // [로그인]

}
