package com.flower.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 회원 도메인 테스트
 */
@DisplayName("Member Domain Tests")
class MemberTest {

    @Test
    @DisplayName("Should create Member instance")
    void shouldCreateMemberInstance() {
        // 준비 및 실행
        Member member = new Member();

        // 검증
        assertNotNull(member);
    }

    @Test
    @DisplayName("Should be able to instantiate multiple members")
    void shouldBeAbleToInstantiateMultipleMembers() {
        // 준비 및 실행
        Member member1 = new Member();
        Member member2 = new Member();

        // 검증
        assertNotNull(member1);
        assertNotNull(member2);
        assertNotSame(member1, member2);
    }

    @Test
    @DisplayName("Should extend Object")
    void shouldExtendObject() {
        // 준비 및 실행
        Member member = new Member();

        // 검증
        assertTrue(member instanceof Object);
    }
}
