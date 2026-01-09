package com.flower.cart.port.out;

import com.flower.cart.dto.MemberInfo;

public interface CartMemberPort {
    MemberInfo getMemberById(Long memberId);
}
