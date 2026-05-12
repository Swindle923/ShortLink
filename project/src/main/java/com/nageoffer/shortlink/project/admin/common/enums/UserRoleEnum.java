package com.nageoffer.shortlink.project.admin.common.enums;

import java.util.Arrays;

public enum UserRoleEnum {
    ADMIN,
    USER;

    public static boolean isValid(String role) {
        return Arrays.stream(values()).anyMatch(each -> each.name().equals(role));
    }
}
