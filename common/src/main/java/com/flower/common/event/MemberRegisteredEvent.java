package com.flower.common.event;

import lombok.Getter;

/**
 * 회원 가입 완료 이벤트
 * 신규 회원이 가입을 성공적으로 완료했을 때 발행됨
 */
@Getter
public class MemberRegisteredEvent extends DomainEvent {

    private final Long memberId;
    private final String email;
    private final String name;

    public MemberRegisteredEvent(Long memberId, String email, String name) {
        super(memberId);
        this.memberId = memberId;
        this.email = email;
        this.name = name;
    }
}
