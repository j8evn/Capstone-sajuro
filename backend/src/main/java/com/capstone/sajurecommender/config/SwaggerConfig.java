package com.capstone.sajurecommender.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / SpringDoc OpenAPI configuration.
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("사주 초개인화 추천 시스템 API")
                        .description("""
                                사용자의 생년월일시를 기반으로 사주팔자를 계산하고,
                                실시간 날씨 및 기분 데이터를 결합하여
                                최적의 장소와 아이템을 추천하는 API입니다.
                                
                                ## 주요 기능
                                - **사주 계산**: 연주/월주/일주/시주 자동 계산 (60갑자, 절기 기반)
                                - **오행 분석**: 오행 분포, 음양 균형, 용신 산출
                                - **날씨 연동**: OpenWeatherMap API + 오행 매핑
                                - **추천 엔진**: 사주·날씨·기분·시간·혼잡도 가중치 기반 점수 산출
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Capstone Team")
                                .email("capstone@example.com"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }
}
