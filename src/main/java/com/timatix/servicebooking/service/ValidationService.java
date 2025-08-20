package com.timatix.servicebooking.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    private static final Pattern LICENSE_PLATE_PATTERN =
            Pattern.compile("^[A-Z]{2}\\d{2,3}[A-Z]{2,3}$");

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPhone(String phone) {
        if (phone == null) return true; // Optional field
        return PHONE_PATTERN.matcher(phone.replaceAll("\\s|-", "")).matches();
    }

    public boolean isValidLicensePlate(String licensePlate) {
        if (licensePlate == null) return true; // Optional field
        return LICENSE_PLATE_PATTERN.matcher(licensePlate.toUpperCase()).matches();
    }

    public boolean isValidPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Za-z].*") &&
                password.matches(".*\\d.*");
    }
}