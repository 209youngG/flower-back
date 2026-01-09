package com.flower.cart.adapter.out;

import com.flower.cart.dto.MemberInfo;
import com.flower.cart.port.out.CartMemberPort;
import com.flower.member.domain.Member;
import com.flower.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartMemberAdapter implements CartMemberPort {

    private final MemberService memberService;

    @Override
    public MemberInfo getMemberById(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        return MemberInfo.builder()
                .id(member.getId())
                .email(member.getEmail())
                .grade(member.getGrade().name())
                .build();
    }
}
