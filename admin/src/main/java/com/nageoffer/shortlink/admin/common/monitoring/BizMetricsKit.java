package com.nageoffer.shortlink.admin.common.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component("adminBizMetricsKit")
@RequiredArgsConstructor
public class BizMetricsKit {

    private final MeterRegistry meterRegistry;

    public void increment(String metricName, String... tags) {
        Counter.builder(metricName)
                .tags(normalizeTags(tags))
                .register(meterRegistry)
                .increment();
    }

    public void increment(String metricName, double amount, String... tags) {
        Counter.builder(metricName)
                .tags(normalizeTags(tags))
                .register(meterRegistry)
                .increment(amount);
    }

    public void recordLatency(String metricName, long nanos, String... tags) {
        Timer.builder(metricName)
                .publishPercentileHistogram(true)
                .tags(normalizeTags(tags))
                .register(meterRegistry)
                .record(nanos, TimeUnit.NANOSECONDS);
    }

    private String[] normalizeTags(String... tags) {
        List<String> normalized = new ArrayList<>();
        if (tags == null) {
            return new String[0];
        }
        for (int i = 0; i < tags.length; i += 2) {
            String key = tags[i] == null ? "unknown_key" : tags[i];
            String value = i + 1 < tags.length && tags[i + 1] != null ? tags[i + 1] : "unknown_value";
            normalized.add(key);
            normalized.add(value);
        }
        return normalized.toArray(new String[0]);
    }
}
