package com.flower.member.service;

import com.flower.common.event.MemberRegisteredEvent;
import com.flower.member.domain.Address;
import com.flower.member.domain.Member;
import com.flower.member.domain.MemberGrade;
import com.flower.member.dto.AddressDto;
import com.flower.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Service Tests")
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    @DisplayName("Should register new member successfully when input is valid")
    void should_register_member_when_input_valid() {
        // given
        String loginId = "testuser";
        String email = "test@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        String name = "Test User";
        String phone = "010-1234-5678";

        given(memberRepository.existsByLoginId(loginId)).willReturn(false);
        given(memberRepository.existsByPhoneNumber(phone)).willReturn(false);
        given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);
        given(memberRepository.save(any(Member.class))).willAnswer(invocation -> {
            Member m = invocation.getArgument(0);
            return Member.builder()
                    .id(1L)
                    .loginId(m.getLoginId())
                    .email(m.getEmail())
                    .password(m.getPassword())
                    .name(m.getName())
                    .phoneNumber(m.getPhoneNumber())
                    .grade(MemberGrade.BRONZE)
                    .pointBalance(BigDecimal.ZERO)
                    .build();
        });

        // when
        Member result = memberService.register(loginId, email, rawPassword, phone, name);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        verify(eventPublisher).publishEvent(any(MemberRegisteredEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when login ID already exists")
    void should_throw_exception_when_login_id_exists() {
        // given
        given(memberRepository.existsByLoginId("duplicate")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> 
            memberService.register("duplicate", "a@b.c", "pw", "010-0000-0000", "name")
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("이미 사용 중인 아이디입니다");
    }

    @Test
    @DisplayName("Should return member addresses when member exists")
    void should_return_addresses_when_member_exists() {
        // given
        Long memberId = 1L;
        Member member = Member.builder().id(memberId).build();
        Address address = Address.builder()
                .id(100L)
                .member(member)
                .street("Main St")
                .city("Seoul")
                .zipCode("12345")
                .build();
        member.addAddress(address);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        List<AddressDto> result = memberService.getMemberAddresses(memberId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).street()).isEqualTo("Main St");
    }
}
