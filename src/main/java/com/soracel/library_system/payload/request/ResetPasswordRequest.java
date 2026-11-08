package com.soracel.library_system.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "El campo no puede estar vacío")
    @NotNull(message = "El token es obligatorio")
    private String resetToken;
    @NotBlank(message = "El campo no puede estar vacío")
    @NotNull(message = "La contraseña es obligatoria")
    private String password;
}
