package com.flower.member.service;

import com.flower.common.event.MemberRegisteredEvent;
import com.flower.member.domain.Address;
import com.flower.member.domain.Member;
import com.flower.member.domain.MemberGrade;
import com.flower.member.domain.PointHistory;
import com.flower.member.domain.PointHistory.PointType;
import com.flower.member.dto.AddAddressRequest;
import com.flower.member.dto.AddressDto;
import com.flower.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<AddressDto> getMemberAddresses(Long memberId) {
        Member member = getMemberById(memberId);
        return member.getAddresses().stream()
                .map(this::toAddressDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addAddress(Long memberId, AddAddressRequest request) {
        Member member = getMemberById(memberId);
        
        if (request.isDefault()) {
            member.getAddresses().forEach(addr -> addr.setIsDefault(false));
        }

        Address address = Address.builder()
                .member(member)
                .recipientName(request.recipientName())
                .recipientPhone(request.recipientPhone())
                .zipCode(request.zipCode())
                .street(request.street())
                .city(request.city())
                .isDefault(request.isDefault())
                .build();
        
        member.addAddress(address);
        memberRepository.save(member);
    }

    private AddressDto toAddressDto(Address address) {
        return new AddressDto(
            address.getId(),
            address.getRecipientName(),
            address.getRecipientPhone(),
            address.getZipCode(),
            address.getStreet(),
            address.getCity(),
            address.getIsDefault()
        );
    }

    @Transactional
    public Member register(String loginId, String email, String password, String phoneNumber, String name, 
                           String zipCode, String addressStr, String detailAddress) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다: " + loginId);
        }

        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다: " + phoneNumber);
        }

        Member member = Member.builder()
                .loginId(loginId)
                .email(email)
                .password(passwordEncoder.encode(password))
                .phoneNumber(phoneNumber)
                .name(name)
                .pointBalance(BigDecimal.ZERO)
                .grade(MemberGrade.BRONZE)
                .build();

        if (zipCode != null && !zipCode.isEmpty() && addressStr != null && !addressStr.isEmpty()) {
            Address address = Address.builder()
                    .member(member)
                    .recipientName(name)
                    .recipientPhone(phoneNumber)
                    .zipCode(zipCode)
                    .street(addressStr)
                    .city(detailAddress != null ? detailAddress : "")
                    .isDefault(true)
                    .build();
            member.addAddress(address);
        }

        member = memberRepository.save(member);

        log.info("신규 회원 가입 완료: ID={}, 이름={}", loginId, name);

        eventPublisher.publishEvent(new MemberRegisteredEvent(member.getId(), email, name));

        return member;
    }

    @Transactional
    public Member register(String loginId, String email, String password, String phoneNumber, String name) {
        return register(loginId, email, password, phoneNumber, name, null, null, null);
    }

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
    }

    @Transactional
    public void earnPoints(Long memberId, BigDecimal points, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));

        BigDecimal oldBalance = member.getPointBalance();
        member.addPoints(points);

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

    @Transactional
    public void usePoints(Long memberId, BigDecimal points, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));

        BigDecimal oldBalance = member.getPointBalance();
        member.usePoints(points);

        PointHistory history = PointHistory.builder()
                .member(member)
                .amount(points.negate())
                .pointType(PointType.USE)
                .description(description)
                .balanceAfter(member.getPointBalance())
                .build();

        member.getPointHistory().add(history);
        memberRepository.save(member);

        log.info("포인트 사용: 회원ID={}, 사용액={}, 이전잔액={}, 현재잔액={}, 사유={}",
                memberId, points, oldBalance, member.getPointBalance(), description);
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + email));
    }

    @Transactional
    public void updateLastLogin(Long memberId) {
        Member member = getMemberById(memberId);
        member.updateLastLogin();
        memberRepository.save(member);
    }
}
