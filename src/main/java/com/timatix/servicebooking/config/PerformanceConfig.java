package com.timatix.servicebooking.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class PerformanceConfig {

    @Bean
    public RequestTimingFilter requestTimingFilter(@Autowired MeterRegistry meterRegistry) {
        return new RequestTimingFilter(meterRegistry);
    }

    public static class RequestTimingFilter extends OncePerRequestFilter {

        private final MeterRegistry meterRegistry;

        public RequestTimingFilter(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain filterChain) throws ServletException, IOException {

            Timer.Sample sample = Timer.start(meterRegistry);
            String uri = request.getRequestURI();
            String method = request.getMethod();

            try {
                filterChain.doFilter(request, response);
            } finally {
                sample.stop(Timer.builder("http.requests")
                        .tag("method", method)
                        .tag("uri", uri)
                        .tag("status", String.valueOf(response.getStatus()))
                        .register(meterRegistry));
            }
        }
    }
}