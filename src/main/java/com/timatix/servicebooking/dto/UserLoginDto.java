package com.timatix.servicebooking.dto;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserLoginDto {

    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
