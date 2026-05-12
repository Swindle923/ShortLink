package com.nageoffer.shortlink.project.common.constant;

public class ShortLinkConstant {

    public static final long DEFAULT_CACHE_VALID_TIME = 2626560000L;

    public static final String AMAP_REMOTE_URL = "https://restapi.amap.com/v3/ip";

    public static final String NULL_CACHE_VALUE = "-";

    public static final long NULL_CACHE_TTL_MINUTES = 30L;

    public static final String NOT_FOUND_REDIRECT = "/page/notfound";

    public static final int UV_COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24 * 30;

    public static final int MAX_SUFFIX_GENERATE_ATTEMPTS = 10;
}
