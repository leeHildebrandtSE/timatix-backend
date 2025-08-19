package com.timatix.servicebooking.dto;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserUpdateDto {

    @NotBlank(message = "Name is required")
    private String name;

    private String phone;
    private String address;
    private String password; // Optional for updates
}
