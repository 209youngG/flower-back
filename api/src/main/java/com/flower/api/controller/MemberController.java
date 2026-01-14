package com.flower.api.controller;

import com.flower.api.security.JwtTokenProvider;
import com.flower.member.domain.Member;
import com.flower.member.dto.LoginRequest;
import com.flower.member.dto.MemberDto;
import com.flower.member.dto.RegisterMemberRequest;
import com.flower.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원 가입", description = "신규 회원을 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<MemberDto> register(@RequestBody RegisterMemberRequest request) {
        Member member = memberService.register(
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.name()
        );
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
        return ResponseEntity.ok(toDto(member, token));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<MemberDto> login(@RequestBody LoginRequest request) {
        Member member = memberService.login(request.email(), request.password());
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
        return ResponseEntity.ok(toDto(member, token));
    }

    private MemberDto toDto(Member member, String token) {
        return new MemberDto(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getPointBalance(),
                member.getGrade(),
                member.getRole(),
                token
        );
    }
}
