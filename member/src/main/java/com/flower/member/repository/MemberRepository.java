package com.flower.member.repository;

import com.flower.member.domain.Member;
import com.flower.member.domain.MemberGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 엔티티를 위한 리포지토리 인터페이스
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일 주소로 회원을 조회합니다.
     */
    Optional<Member> findByEmail(String email);

    /**
     * 전화번호로 회원을 조회합니다.
     */
    Optional<Member> findByPhoneNumber(String phoneNumber);

    /**
     * 이메일 존재 여부를 확인합니다.
     */
    boolean existsByEmail(String email);

    /**
     * 전화번호 존재 여부를 확인합니다.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * 등급별 회원을 조회합니다.
     */
    java.util.List<Member> findByGrade(MemberGrade grade);
}
