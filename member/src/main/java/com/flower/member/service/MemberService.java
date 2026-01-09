package com.flower.member.service;

import com.flower.common.event.MemberRegisteredEvent;
import com.flower.member.domain.Member;
import com.flower.member.domain.MemberGrade;
import com.flower.member.domain.PointHistory;
import com.flower.member.domain.PointHistory.PointType;
import com.flower.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 신규 회원 가입
     */
    @Transactional
    public Member register(String email, String password, String phoneNumber, String name) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다: " + email);
        }

        // 전화번호 중복 확인
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다: " + phoneNumber);
        }

        // 회원 생성
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .name(name)
                .pointBalance(BigDecimal.ZERO)
                .grade(MemberGrade.BRONZE)
                .build();

        member = memberRepository.save(member);

        log.info("신규 회원 가입 완료: 이메일={}, 전화번호={}", email, phoneNumber);

        eventPublisher.publishEvent(new MemberRegisteredEvent(member.getId(), email, name));

        return member;
    }

    /**
     * 회원 로그인 (자격 증명이 유효한 경우 회원 정보 반환)
     */
    public Member login(String email, String password) {
        return memberRepository.findByEmail(email)
                .filter(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));
    }

    /**
     * 포인트 적립
     */
    @Transactional
    public void earnPoints(Long memberId, BigDecimal points, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));

        // 기존 잔액 기록
        BigDecimal oldBalance = member.getPointBalance();

        // 포인트 추가
        member.addPoints(points);

        // 포인트 이력 생성
        PointHistory history = PointHistory.builder()
                .member(member)
                .amount(points)
                .pointType(PointType.EARN)
                .description(description)
                .balanceAfter(member.getPointBalance())
                .build();

        member.getPointHistory().add(history);
        memberRepository.save(member);

        log.info("포인트 적립: 회원ID={}, 적립액={}, 이전잔액={}, 현재잔액={}, 사유={}",
                memberId, points, oldBalance, member.getPointBalance(), description);
    }

    /**
     * 포인트 사용
     */
    @Transactional
    public void usePoints(Long memberId, BigDecimal points, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));

        // 기존 잔액 기록
        BigDecimal oldBalance = member.getPointBalance();

        // 포인트 사용 (잔액 부족 시 예외 발생)
        member.usePoints(points);

        // 포인트 이력 생성
        PointHistory history = PointHistory.builder()
                .member(member)
                .amount(points.negate())  // 사용은 음수로 기록
                .pointType(PointType.USE)
                .description(description)
                .balanceAfter(member.getPointBalance())
                .build();

        member.getPointHistory().add(history);
        memberRepository.save(member);

        log.info("포인트 사용: 회원ID={}, 사용액={}, 이전잔액={}, 현재잔액={}, 사유={}",
                memberId, points, oldBalance, member.getPointBalance(), description);
    }

    /**
     * ID로 회원 조회
     */
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));
    }

    /**
     * 이메일로 회원 조회
     */
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + email));
    }

    /**
     * 마지막 로그인 시간 갱신
     */
    @Transactional
    public void updateLastLogin(Long memberId) {
        Member member = getMemberById(memberId);
        member.updateLastLogin();
        memberRepository.save(member);
    }
}
