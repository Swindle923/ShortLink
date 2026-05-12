package com.nageoffer.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.common.enums.UserRoleEnum;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RequiredArgsConstructor
public class UserAuthenticationFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestUri = httpServletRequest.getRequestURI();
        if (!requestUri.startsWith("/api/short-link/admin/v1/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (Objects.equals(httpServletRequest.getMethod(), "OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (isPublicApi(requestUri, httpServletRequest.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = decodeHeader(httpServletRequest.getHeader("Username"));
        String token = httpServletRequest.getHeader("Token");
        if (StrUtil.isBlank(username) || StrUtil.isBlank(token)) {
            returnJson(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "登录已失效，请重新登录");
            return;
        }
        Object loginUserJson = stringRedisTemplate.opsForHash().get(RedisCacheConstant.USER_LOGIN_KEY + username, token);
        if (loginUserJson == null) {
            returnJson(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, "登录已失效，请重新登录");
            return;
        }
        if (isAdminApi(requestUri)) {
            UserDO userDO = JSON.parseObject(loginUserJson.toString(), UserDO.class);
            String role = userDO == null ? null : userDO.getRole();
            if (StrUtil.isBlank(role) && Objects.equals(username, "admin")) {
                role = UserRoleEnum.ADMIN.name();
            }
            if (!Objects.equals(role, UserRoleEnum.ADMIN.name())) {
                returnJson(httpServletResponse, HttpServletResponse.SC_FORBIDDEN, "无权限执行该操作");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicApi(String requestUri, String method) {
        if (Objects.equals(requestUri, "/api/short-link/admin/v1/user/login")) {
            return true;
        }
        if (Objects.equals(requestUri, "/api/short-link/admin/v1/user") && Objects.equals(method, "POST")) {
            return true;
        }
        return Objects.equals(requestUri, "/api/short-link/admin/v1/user/has-username");
    }

    private boolean isAdminApi(String requestUri) {
        return Objects.equals(requestUri, "/api/short-link/admin/v1/user/role");
    }

    private String decodeHeader(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return value;
        }
    }

    private void returnJson(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JSON.toJSONString(Results.failure("A000401", message)));
        }
    }
}
