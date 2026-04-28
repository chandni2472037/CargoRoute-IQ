package com.example.demo.aspects;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.demo.DTO.AuditLogDTO;
import com.example.demo.annotations.AuditableAction;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuditLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogAspect.class);

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public AuditLogAspect(AuditLogService auditLogService, UserRepository userRepository) {
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @AfterReturning(pointcut = "@annotation(auditableAction)", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, AuditableAction auditableAction, Object result) {
        try {
            AuditLogDTO dto = new AuditLogDTO();
            dto.setAction(auditableAction.action().name());
            dto.setResourceType(auditableAction.resourceType().name());
            dto.setDetails(resolveDetails(joinPoint, auditableAction));

            Long userId = resolveUserId(joinPoint, result);
            Long resourceId = resolveResourceId(joinPoint, result, auditableAction.resourceIdArgIndex());

            dto.setUserID(userId);
            dto.setResourceID(resourceId);

            auditLogService.saveAuditLog(dto);
        } catch (Exception ex) {
            logger.warn("Audit logging failed for method: {}", joinPoint.getSignature().toShortString(), ex);
        }
    }

    private String resolveDetails(JoinPoint joinPoint, AuditableAction auditableAction) {
        if (!auditableAction.details().isBlank()) {
            return auditableAction.details();
        }
        return "Action executed via " + joinPoint.getSignature().getName();
    }

    private Long resolveUserId(JoinPoint joinPoint, Object result) {
        Long requestUserId = getUserIdFromRequest();
        if (requestUserId != null) {
            return requestUserId;
        }

        Long resultUserId = extractLongProperty(result, "getUserID", "getId");
        if (resultUserId != null) {
            return resultUserId;
        }

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            Long argUserId = extractLongProperty(arg, "getUserID", "getId");
            if (argUserId != null) {
                return argUserId;
            }

            String email = extractStringProperty(arg, "getEmail", "getUsername");
            if (email != null && !email.isBlank()) {
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    return user.getUserID();
                }
            }
        }

        return null;
    }

    private Long resolveResourceId(JoinPoint joinPoint, Object result, int resourceIdArgIndex) {
        Object[] args = joinPoint.getArgs();

        if (resourceIdArgIndex >= 0 && resourceIdArgIndex < args.length) {
            Long fromArg = extractLong(args[resourceIdArgIndex]);
            if (fromArg != null) {
                return fromArg;
            }
        }

        Long fromResult = extractLongProperty(result, "getUserID", "getAuditID", "getId");
        if (fromResult != null) {
            return fromResult;
        }

        return null;
    }

    private Long getUserIdFromRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return null;
        }

        HttpServletRequest request = servletAttributes.getRequest();
        Object userId = request.getAttribute("userId");
        if (userId == null) {
            return null;
        }

        return extractLong(userId);
    }

    private Long extractLongProperty(Object source, String... getterNames) {
        if (source == null || getterNames == null) {
            return null;
        }

        for (String getterName : getterNames) {
            Long value = invokeGetter(source, getterName);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private Long invokeGetter(Object source, String getterName) {
        try {
            Method method = source.getClass().getMethod(getterName);
            Object value = method.invoke(source);
            return extractLong(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractStringProperty(Object source, String... getterNames) {
        if (source == null || getterNames == null) {
            return null;
        }

        for (String getterName : getterNames) {
            try {
                Method method = source.getClass().getMethod(getterName);
                Object value = method.invoke(source);
                if (value instanceof String stringValue) {
                    return stringValue;
                }
            } catch (Exception ignored) {
                // try next getter
            }
        }

        return null;
    }

    private Long extractLong(Object value) {
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Integer intValue) {
            return intValue.longValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Long.valueOf(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
