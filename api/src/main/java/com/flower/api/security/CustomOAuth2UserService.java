package com.flower.api.security;

import com.flower.member.domain.Member;
import com.flower.member.domain.MemberRole;
import com.flower.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oauth2User.getAttribute("sub"); // Google default
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        log.info("OAuth2 Login: provider={}, email={}", provider, email);

        Member member = saveOrUpdateMember(provider, providerId, email, name);

        return new UserPrincipal(member, oauth2User.getAttributes());
    }

    private Member saveOrUpdateMember(String provider, String providerId, String email, String name) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (member.getProvider() == null) {
                // 기존 일반 가입자라면 소셜 연동 정보 업데이트
                log.info("Existing member linking with OAuth2: {}", email);
                member.setProvider(provider);
                member.setProviderId(providerId);
                // 이름이 변경되었을 수도 있으므로 업데이트 고려 가능
            }
            return member;
        } else {
            // 신규 가입
            log.info("New OAuth2 member registration: {}", email);
            Member newMember = Member.builder()
                    .email(email)
                    .name(name != null ? name : "Unknown")
                    .loginId(email) // 소셜 로그인은 이메일을 ID로 사용
                    .password(UUID.randomUUID().toString()) // 임의의 비밀번호
                    .phoneNumber("000-0000-0000") // 필수값이므로 임시값
                    .role(MemberRole.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            return memberRepository.save(newMember);
        }
    }
}
