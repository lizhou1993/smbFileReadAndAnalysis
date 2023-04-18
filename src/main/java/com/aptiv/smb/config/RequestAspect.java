package com.aptiv.smb.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Slf4j
@Aspect
@AllArgsConstructor
@Component
public class RequestAspect {

    @Before("execution(* com.aptiv.smb.controller..*.*(..))")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("收到请求 | URL:");
            stringBuilder.append(request.getRequestURL());
            stringBuilder.append(" | HTTP_METHOD:");
            stringBuilder.append(request.getMethod());
            stringBuilder.append(" | IP:");
            stringBuilder.append(request.getRemoteAddr());
            stringBuilder.append(" | CLASS_METHOD:");
            stringBuilder.append(joinPoint.getSignature().getDeclaringTypeName());
            stringBuilder.append(".");
            stringBuilder.append(joinPoint.getSignature().getName());
            stringBuilder.append(" | ARGS:");
            stringBuilder.append(Arrays.toString(joinPoint.getArgs()));
            log.info(stringBuilder.toString());
        }
    }

}
