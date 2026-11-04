package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse login(String username, String password);
    AuthResponse signup(UserDTO request);

    void createPasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}
