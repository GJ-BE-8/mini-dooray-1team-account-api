package main.accountapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.web.servlet.function.RequestPredicates.headers;


@Configuration
public class SecurityConfig{ // 비밀번호 암호화를 위해 사용 비밀번호는 처음부터 암호화를 해야함

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // H2 콘솔을 허용
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")) // CSRF 비활성화, H2 콘솔 예외 처리
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 접근 허용
                        .anyRequest().authenticated())  // 다른 모든 요청은 인증 필요
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()));  // H2 콘솔을 iframe으로 열 수 있도록 설정

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
