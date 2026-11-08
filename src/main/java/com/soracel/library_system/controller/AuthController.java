package com.soracel.library_system.controller;

import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.payload.request.ForgotPasswordRequest;
import com.soracel.library_system.payload.request.LoginRequest;
import com.soracel.library_system.payload.request.ResetPasswordRequest;
import com.soracel.library_system.payload.response.ApiResponse;
import com.soracel.library_system.payload.response.AuthResponse;
import com.soracel.library_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody UserDTO userDto){
        AuthResponse authResponse = authService.signup(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        AuthResponse authResponse = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        authService.createPasswordResetToken(request.getEmail());

        ApiResponse resp = ApiResponse.builder()
                .message("Se ha enviado un link a tu dirección de correo para restablecer la contraseña")
                .status(true)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        authService.resetPassword(request.getResetToken(), request.getPassword());
        ApiResponse resp = ApiResponse.builder()
                .message("Se ha restablecido su contraseña")
                .status(true)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
