package com.flower.api.controller;

import com.flower.api.security.JwtTokenProvider;
import com.flower.member.domain.Member;
import com.flower.member.dto.LoginRequest;
import com.flower.member.dto.MemberDto;
import com.flower.member.dto.RegisterMemberRequest;
import com.flower.member.dto.AddAddressRequest;
import com.flower.member.dto.AddressDto;
import com.flower.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String loginId = request.loginId() != null ? request.loginId() : request.email();
        
        Member member = memberService.register(
                loginId,
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.name(),
                request.zipCode(),
                request.address(),
                request.detailAddress()
        );
        String token = jwtTokenProvider.createToken(member.getLoginId(), member.getRole().name());
        return ResponseEntity.ok(toDto(member, token));
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<MemberDto> login(@RequestBody LoginRequest request) {
        Member member = memberService.login(request.loginId(), request.password());
        String token = jwtTokenProvider.createToken(member.getLoginId(), member.getRole().name());
        return ResponseEntity.ok(toDto(member, token));
    }

    @Operation(summary = "회원 주소 목록 조회", description = "회원의 배송 주소 목록을 조회합니다.")
    @GetMapping("/{memberId}/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberAddresses(memberId));
    }

    @Operation(summary = "회원 주소 추가", description = "회원의 배송 주소를 추가합니다.")
    @PostMapping("/{memberId}/addresses")
    public ResponseEntity<Void> addAddress(@PathVariable Long memberId, @RequestBody AddAddressRequest request) {
        memberService.addAddress(memberId, request);
        return ResponseEntity.ok().build();
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
