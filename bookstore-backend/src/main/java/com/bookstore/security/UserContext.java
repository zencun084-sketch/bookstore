package com.bookstore.security;

import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 当前登录用户上下文
 */
@Component
public class UserContext {

    /**
     * 获取当前登录用户ID
     */
    public static Long getCurrentUserId() {
        HttpServletRequest request = getRequest();
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getCurrentUsername() {
        return (String) getRequest().getAttribute("username");
    }

    /**
     * 获取当前登录用户角色
     */
    public static String getCurrentRole() {
        return (String) getRequest().getAttribute("role");
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentRole());
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return attributes.getRequest();
    }
}
