package com.nageoffer.shortlink.project.console.aspect;

import com.alibaba.fastjson2.JSON;
import com.nageoffer.shortlink.project.common.biz.user.UserContext;
import com.nageoffer.shortlink.project.console.dao.entity.AdminAuditLogDO;
import com.nageoffer.shortlink.project.console.dao.mapper.AdminAuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuditAspect {

    private final AdminAuditLogMapper auditLogMapper;

    @Around("@annotation(com.nageoffer.shortlink.project.console.aspect.AdminAudit)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Method method = sig.getMethod();
        AdminAudit audit = method.getAnnotation(AdminAudit.class);

        AdminAuditLogDO logDO = new AdminAuditLogDO();
        logDO.setAdminUsername(UserContext.getUsername());
        logDO.setActionType(audit.actionType());
        logDO.setTargetType(audit.targetType());
        logDO.setRequestParams(safeJson(pjp.getArgs()));
        logDO.setIp(currentIp());
        logDO.setSuccess(1);
        Date now = new Date();
        logDO.setCreateTime(now);
        logDO.setUpdateTime(now);
        logDO.setDelFlag(0);

        Object result;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            logDO.setSuccess(0);
            logDO.setErrorMessage(trunc(ex.getMessage(), 1000));
            safeInsert(logDO);
            throw ex;
        }
        safeInsert(logDO);
        return result;
    }

    private void safeInsert(AdminAuditLogDO logDO) {
        try {
            auditLogMapper.insert(logDO);
        } catch (Exception ex) {
            log.warn("audit log insert fail: {}", ex.getMessage());
        }
    }

    private String safeJson(Object[] args) {
        try {
            return trunc(JSON.toJSONString(args), 2000);
        } catch (Exception ex) {
            return null;
        }
    }

    private String trunc(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() > max ? s.substring(0, max) : s;
    }

    private String currentIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return null;
            }
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim();
            }
            return req.getRemoteAddr();
        } catch (Exception ex) {
            return null;
        }
    }
}
