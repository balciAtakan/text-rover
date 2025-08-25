package com.textrover.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.textrover.dto.generated.ErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingConfig implements HandlerInterceptor {

    private static final Logger log = LogManager.getLogger(RateLimitingConfig.class);
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Rate limit: 10 requests per minute per IP
    private static final int CAPACITY = 10;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String clientIp = getClientIpAddress(request);
        
        Bucket bucket = cache.computeIfAbsent(clientIp, this::createNewBucket);
        
        if (bucket.tryConsume(1)) {
            log.debug("Rate limit check passed for IP: {}", clientIp);
            return true;
        } else {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            ErrorResponse errorResponse = new ErrorResponse()
                    .error("RATE_LIMIT_EXCEEDED")
                    .message("Too many requests. Please try again later.")
                    .timestamp(OffsetDateTime.now());
            
            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }
    }

    private Bucket createNewBucket(String key) {
        Bandwidth limit = Bandwidth.classic(CAPACITY, Refill.intervally(CAPACITY, REFILL_DURATION));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
