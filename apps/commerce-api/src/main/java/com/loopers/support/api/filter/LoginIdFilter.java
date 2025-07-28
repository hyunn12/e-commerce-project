package com.loopers.support.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class LoginIdFilter implements Filter {

    public static final String USER_LOGIN_ID_HEADER = "X-USER-ID";
    public static final String USER_LOGIN_ID_ATTR = "loginId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;

        String loginId = httpServletRequest.getHeader(USER_LOGIN_ID_HEADER);
        if (loginId == null || loginId.isEmpty()) {
            ((HttpServletResponse) servletResponse).sendError(HttpStatus.BAD_REQUEST.value(), USER_LOGIN_ID_HEADER +" 헤더가 존재하지 않습니다.");
            return;
        }

        httpServletRequest.setAttribute(USER_LOGIN_ID_ATTR, loginId);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
