package com.loopers.support.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class UserIdFilter implements Filter {

    public static final String USER_ID_HEADER = "X-USER-ID";
    public static final String USER_ID_ATTR = "userId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest= (HttpServletRequest) servletRequest;
        String uri = httpServletRequest.getRequestURI();

        if (uri.startsWith("/api/") && !uri.equals("/api/v1/users")) {

            String userId = httpServletRequest.getHeader(USER_ID_HEADER);
            if (userId == null || userId.isEmpty()) {
                ((HttpServletResponse) servletResponse).sendError(HttpStatus.UNAUTHORIZED.value(), USER_ID_HEADER+" 헤더가 존재하지 않습니다.");
                return;
            }

            httpServletRequest.setAttribute(USER_ID_ATTR, userId);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
