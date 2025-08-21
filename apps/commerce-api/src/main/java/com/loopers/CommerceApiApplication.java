package com.loopers;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

import java.util.TimeZone;

@OpenAPIDefinition(
        info = @Info(
                title = "이커머스 플랫폼 API",
                description = "쇼핑·주문·결제 기능을 제공하는 이커머스 플랫폼 API"
        )
)
@EnableRetry
@EnableFeignClients
@ConfigurationPropertiesScan
@SpringBootApplication
public class CommerceApiApplication {

    @PostConstruct
    public void started() {
        // set timezone
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(CommerceApiApplication.class, args);
    }
}
