package com.flower.api.config;

import com.flower.member.domain.Member;
import com.flower.member.domain.MemberGrade;
import com.flower.member.domain.MemberRole;
import com.flower.member.repository.MemberRepository;
import com.flower.product.domain.Product;
import com.flower.product.domain.ProductCategory;
import com.flower.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initMembers();
        initProducts();
    }

    private void initMembers() {
        if (memberRepository.findByLoginId("root").isPresent()) return;

        log.info("Initializing Seed Data: Members");

        memberRepository.save(Member.builder()
                .loginId("root") // 관리자 ID
                .email("root@flower.com")
                .password(passwordEncoder.encode("root1234"))
                .name("Root Admin")
                .phoneNumber("010-0000-0000")
                .role(MemberRole.ROOT_ADMIN)
                .grade(MemberGrade.GOLD)
                .build());

        memberRepository.save(Member.builder()
                .loginId("product_admin") // 상품 관리자 ID
                .email("product@flower.com")
                .password(passwordEncoder.encode("product1234"))
                .name("Product Admin")
                .phoneNumber("010-1111-1111")
                .role(MemberRole.PRODUCT_ADMIN)
                .grade(MemberGrade.SILVER)
                .build());

        memberRepository.save(Member.builder()
                .loginId("user@flower.com") // 일반 회원은 이메일을 ID로 사용
                .email("user@flower.com")
                .password(passwordEncoder.encode("user1234"))
                .name("Normal User")
                .phoneNumber("010-2222-2222")
                .role(MemberRole.USER)
                .grade(MemberGrade.BRONZE)
                .build());
    }

    private void initProducts() {
        if (productRepository.count() > 0) return;

        log.info("Initializing Seed Data: Products");

        productRepository.save(Product.builder()
                .productCode("P001")
                .name("시드 데이터 - 빨간 장미")
                .description("아주 붉고 예쁜 장미입니다.")
                .price(BigDecimal.valueOf(15000))
                .stockQuantity(100)
                .category(ProductCategory.FLOWER_BOUQUET)
                .deliveryType(Product.DeliveryType.PARCEL)
                .isActive(true)
                .build());

        productRepository.save(Product.builder()
                .productCode("P002")
                .name("시드 데이터 - 하얀 백합")
                .description("순결한 백합 꽃다발")
                .price(BigDecimal.valueOf(22000))
                .stockQuantity(50)
                .category(ProductCategory.FLOWER_BOUQUET)
                .deliveryType(Product.DeliveryType.PARCEL)
                .isActive(true)
                .build());
    }
}
