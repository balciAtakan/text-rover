package com.textrover.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RequestMDCInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "requestId";
    private static final String METHOD = "method";
    private static final String URL = "url";
    private static final String CLIENT_IP = "clientIp";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString().substring(0, 16);

        String clientIp = RateLimitingConfig.getClientIpAddress(request);
        
        // Populate MDC
        ThreadContext.put(REQUEST_ID, requestId);
        ThreadContext.put(METHOD, request.getMethod());
        ThreadContext.put(URL, request.getRequestURI());
        ThreadContext.put(CLIENT_IP, clientIp);
        
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        // Clear MDC to prevent memory leaks
        ThreadContext.clearAll();
    }
}
