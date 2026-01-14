package com.flower.api.config;

import com.flower.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 회원가입, 로그인은 누구나 접근 가능
                .requestMatchers(HttpMethod.POST, "/api/v1/members/register", "/api/v1/members/login").permitAll()
                
                // 상품 조회는 누구나 가능
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                
                // 상품 관리(등록/수정/삭제)는 ROOT_ADMIN 또는 PRODUCT_ADMIN 가능
                .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ROOT_ADMIN", "PRODUCT_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("ROOT_ADMIN", "PRODUCT_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasAnyRole("ROOT_ADMIN", "PRODUCT_ADMIN")
                
                // 배송 관리
                .requestMatchers(HttpMethod.PUT, "/api/v1/deliveries/**").hasAnyRole("ROOT_ADMIN", "DELIVERY_ADMIN")
                
                // Swagger UI 허용
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
