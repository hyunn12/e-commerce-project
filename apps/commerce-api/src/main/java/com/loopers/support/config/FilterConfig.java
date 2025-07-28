package com.loopers.support.config;

import com.loopers.support.api.filter.LoginIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    FilterRegistrationBean<LoginIdFilter> tokenFilter() {
        FilterRegistrationBean<LoginIdFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new LoginIdFilter());
        registrationBean.addUrlPatterns(
                "/api/v1/users/me",
                "/api/v1/points",
                "/api/v1/points/**"
        );
        registrationBean.setOrder(1);
        registrationBean.setName("loginIdFilter");

        return registrationBean;
    }
}
