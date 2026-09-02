package com.bookstore.security;

import com.bookstore.common.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 去除 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtils.validateToken(token)) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null) {
            writeError(response, ResultCode.UNAUTHORIZED);
            return false;
        }

        // 将用户信息存入 request 供 Controller 使用
        request.setAttribute("userId", claims.get("userId"));
        request.setAttribute("username", claims.getSubject());
        request.setAttribute("role", claims.get("role"));
        return true;
    }

    private void writeError(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        Map<String, Object> result = new HashMap<>();
        result.put("code", resultCode.getCode());
        result.put("message", resultCode.getMessage());
        result.put("data", null);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
