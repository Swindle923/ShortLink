package com.nageoffer.shortlink.aggregation.config.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class ServiceMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around("execution(* com.nageoffer.shortlink.project.admin.service.impl..*(..)) || execution(* com.nageoffer.shortlink.project.service.impl..*(..))")
    public Object aroundServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String simpleClassName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String module = className.contains(".admin.") ? "admin" : "project";
        long startNanos = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            Counter.builder("shortlink.biz.service.calls")
                    .description("Service 方法调用次数")
                    .tag("module", module)
                    .tag("class", simpleClassName)
                    .tag("method", methodName)
                    .tag("result", "success")
                    .register(meterRegistry)
                    .increment();
            return result;
        } catch (Throwable ex) {
            Counter.builder("shortlink.biz.service.calls")
                    .description("Service 方法调用次数")
                    .tag("module", module)
                    .tag("class", simpleClassName)
                    .tag("method", methodName)
                    .tag("result", "error")
                    .tag("exception", ex.getClass().getSimpleName())
                    .register(meterRegistry)
                    .increment();
            throw ex;
        } finally {
            Timer.builder("shortlink.biz.service.latency")
                    .description("Service 方法耗时")
                    .publishPercentileHistogram(true)
                    .tag("module", module)
                    .tag("class", simpleClassName)
                    .tag("method", methodName)
                    .register(meterRegistry)
                    .record(System.nanoTime() - startNanos, TimeUnit.NANOSECONDS);
        }
    }
}
