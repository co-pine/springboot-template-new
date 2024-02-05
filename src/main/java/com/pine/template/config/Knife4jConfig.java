package com.pine.template.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Knife4j 接口文档配置
 * <a href="https://doc.xiaominfo.com/knife4j/documentation/get_start.html">官方文档</a>
 *
 * @author pine
 */
@Configuration
@EnableKnife4j
@Profile({"dev", "test", "local"})
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("SpringBoot3 template")
                        .description("后端模版 new")
                        .contact(
                                new Contact()
                                        .name("松柏")
                                        .email("18339461129@163.com")
                                        .url("https://github.com/co-pine")
                        )
                        .version("0.0.1")
        );
    }
}