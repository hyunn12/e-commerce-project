package com.loopers.support.config;

import com.loopers.support.api.filter.UserIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    FilterRegistrationBean<UserIdFilter> tokenFilter() {
        FilterRegistrationBean<UserIdFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new UserIdFilter());
        registrationBean.addUrlPatterns(
                "/api/v1/users/me",
                "/api/v1/points",
                "/api/v1/points/**"
        );
        registrationBean.setOrder(1);
        registrationBean.setName("userIdLFilter");

        return registrationBean;
    }
}
