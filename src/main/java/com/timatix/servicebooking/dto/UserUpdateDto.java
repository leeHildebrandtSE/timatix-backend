package com.timatix.servicebooking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String phone;
    private String address;
    private String password; // Optional for updates
}