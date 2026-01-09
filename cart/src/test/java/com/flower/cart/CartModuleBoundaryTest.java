package com.flower.cart;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@Disabled
@DisplayName("모듈 경계 테스트 - Cart 모듈")
class CartModuleBoundaryTest {

    private static final String CART_PACKAGE = "com.flower.cart";
    private static final String MEMBER_DOMAIN_PACKAGE = "com.flower.member.domain";
    private static final String PRODUCT_DOMAIN_PACKAGE = "com.flower.product.domain";

    @Test
    @DisplayName("Cart 엔티티는 Member 도메인 엔티티에 의존해서는 안 된다")
    void cartEntityShouldNotDependOnMemberDomainEntities() {
        JavaClasses cartClasses = new ClassFileImporter().importPackages(CART_PACKAGE);

        noClasses().that().haveSimpleName("Cart")
                .should().dependOnClassesThat().resideInAPackage(MEMBER_DOMAIN_PACKAGE)
                .because("Cart는 Member 도메인과 분리되어야 함")
                .check(cartClasses);
    }

    @Test
    @DisplayName("CartItem 엔티티는 Product 도메인 엔티티에 직접 의존해서는 안 된다")
    void cartItemEntityShouldNotDependOnProductDomainEntitiesDirectly() {
        JavaClasses cartClasses = new ClassFileImporter().importPackages(CART_PACKAGE);

        noClasses().that().haveSimpleName("CartItem")
                .should().dependOnClassesThat().resideInAPackage(PRODUCT_DOMAIN_PACKAGE + "..Product")
                .because("CartItem은 Product 엔티티 대신 productId를 사용해야 함")
                .check(cartClasses);
    }
}
