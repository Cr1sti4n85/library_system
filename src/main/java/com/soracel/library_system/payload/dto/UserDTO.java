package com.soracel.library_system.payload.dto;

import com.soracel.library_system.domain.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    @NotNull(message = "El email es obligatorio")
    private String email;
    @NotNull(message = "La contraseña es obligatoria")
    private String password;
    private String phone;
    @NotNull(message = "El nombre es obligatorio")
    private String fullName;
    private UserRole role;
    private String username;

    private LocalDateTime lastLogin;
}
