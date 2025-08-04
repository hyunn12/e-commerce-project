package com.loopers.support.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_ATTR;
import static com.loopers.support.constants.HeaderConstants.USER_USER_ID_HEADER;

@Slf4j
public class UserIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String value = request.getHeader(USER_USER_ID_HEADER);
        if (value == null || value.isBlank()) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), USER_USER_ID_HEADER +" 헤더가 존재하지 않습니다.");
            return;
        }

        try {
            Long userId = Long.valueOf(value);
            if (userId <= 0L) {
                response.sendError(HttpStatus.BAD_REQUEST.value(), USER_USER_ID_HEADER + " 헤더 값이 유효하지 않습니다.");
                return;
            }

            request.setAttribute(USER_USER_ID_ATTR, userId);
            filterChain.doFilter(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), USER_USER_ID_HEADER + " 헤더 값의 형식이 잘못되었습니다.");
        }
    }
}
