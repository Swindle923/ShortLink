package com.nageoffer.shortlink.project.console.config;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.nageoffer.shortlink.project.admin.common.biz.user.HeaderUtils;
import com.nageoffer.shortlink.project.admin.common.constant.RedisCacheConstant;
import com.nageoffer.shortlink.project.admin.common.enums.UserRoleEnum;
import com.nageoffer.shortlink.project.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.common.biz.user.UserInfoDTO;
import com.nageoffer.shortlink.project.common.convention.result.Results;
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
import java.util.Objects;

@RequiredArgsConstructor
public class AdminRoleFilter implements Filter {

    private static final String CONSOLE_API_PREFIX = "/api/short-link/console/v1/";

    private static final String CONSOLE_LOGIN_URI = "/api/short-link/console/v1/login";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        if (!uri.startsWith(CONSOLE_API_PREFIX) || "OPTIONS".equals(req.getMethod()) || uri.equals(CONSOLE_LOGIN_URI)) {
            chain.doFilter(request, response);
            return;
        }

        String username = HeaderUtils.decode(req.getHeader(HeaderUtils.HEADER_USERNAME_UPPER));
        String token = req.getHeader(HeaderUtils.HEADER_TOKEN);
        if (StrUtil.isBlank(username) || StrUtil.isBlank(token)) {
            writeFail(resp, HttpServletResponse.SC_UNAUTHORIZED, "管理员未登录或登录已失效");
            return;
        }

        Object loginUserJson = stringRedisTemplate.opsForHash().get(RedisCacheConstant.USER_LOGIN_KEY + username, token);
        if (loginUserJson == null) {
            writeFail(resp, HttpServletResponse.SC_UNAUTHORIZED, "管理员未登录或登录已失效");
            return;
        }

        UserDO userDO = JSON.parseObject(loginUserJson.toString(), UserDO.class);
        if (userDO == null || !Objects.equals(userDO.getRole(), UserRoleEnum.ADMIN.name())) {
            writeFail(resp, HttpServletResponse.SC_FORBIDDEN, "仅管理员允许访问该接口");
            return;
        }
        if (userDO.getStatus() != null && userDO.getStatus() == 1) {
            writeFail(resp, HttpServletResponse.SC_FORBIDDEN, "账户已被冻结");
            return;
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO(
                userDO.getId() == null ? null : userDO.getId().toString(),
                userDO.getUsername(),
                userDO.getRealName()
        );
        UserContext.setUser(userInfoDTO);
        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.removeUser();
        }
    }

    private void writeFail(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=utf-8");
        try (PrintWriter w = resp.getWriter()) {
            w.print(JSON.toJSONString(Results.failure("C000401", msg)));
        }
    }
}
