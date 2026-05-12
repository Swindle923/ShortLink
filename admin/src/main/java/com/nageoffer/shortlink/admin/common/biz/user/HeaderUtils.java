package com.nageoffer.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class HeaderUtils {

    public static final String HEADER_USERNAME_UPPER = "Username";

    public static final String HEADER_USERNAME_LOWER = "username";

    public static final String HEADER_TOKEN = "Token";

    public static final String HEADER_USER_ID = "userId";

    public static final String HEADER_REAL_NAME = "realName";

    private HeaderUtils() {
    }

    public static String decode(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return value;
        }
    }
}
