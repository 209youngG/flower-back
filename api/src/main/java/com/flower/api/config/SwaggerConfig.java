package com.flower.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (OpenAPI) 설정 클래스
 * API 문서를 자동으로 생성하고 관리하기 위한 설정을 담당합니다.
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정을 정의하는 Bean
     * API의 제목, 설명, 버전 등 기본 정보를 설정합니다.
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flower Shop API") // API 제목
                        .description("Flower Shop Application API Documentation") // API 설명
                        .version("v1.0.0")); // API 버전
    }
}
