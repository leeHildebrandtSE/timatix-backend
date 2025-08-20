package com.timatix.servicebooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
@Configuration
public class ApplicationConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Register JavaTimeModule for Java 8 time support
        mapper.registerModule(new JavaTimeModule());

        // Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Set timezone to South Africa
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.of("Africa/Johannesburg")));

        log.info("Configured ObjectMapper with South African timezone");
        return mapper;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    // Set default timezone for the application
    static {
        TimeZone.setDefault(TimeZone.getTimeZone("Africa/Johannesburg"));
    }
}