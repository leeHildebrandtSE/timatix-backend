package com.timatix.servicebooking.dto;

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private User.Role role;
    // Exclude password for security
}
