package com.nageoffer.shortlink.project.common.constant;

public class RedisKeyConstant {

    public static final String GOTO_SHORT_LINK_KEY = "short-link:goto:%s";

    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short-link:is-null:goto_%s";

    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link:lock:goto:%s";

    public static final String LOCK_GID_UPDATE_KEY = "short-link:lock:update-gid:%s";

    public static final String DELAY_QUEUE_STATS_KEY = "short-link:delay-queue:stats";

    public static final String SHORT_LINK_STATS_UV_KEY = "short-link:stats:uv:";

    public static final String SHORT_LINK_STATS_UIP_KEY = "short-link:stats:uip:";

    public static final String SHORT_LINK_STATS_STREAM_TOPIC_KEY = "short-link:stats-stream";

    public static final String SHORT_LINK_STATS_STREAM_GROUP_KEY = "short-link:stats-stream:only-group";

    public static final String SHORT_LINK_CREATE_LOCK_KEY = "short-link:lock:create";
}
