package com.soracel.library_system.service.impl;

import com.soracel.library_system.configurations.JwtUtil;
import com.soracel.library_system.domain.UserRole;
import com.soracel.library_system.exception.InvalidEmailException;
import com.soracel.library_system.mapper.UserMapper;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.payload.response.AuthResponse;
import com.soracel.library_system.repository.UserRepository;
import com.soracel.library_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil  jwtUtil;

    @Override
    public AuthResponse login(String username, String password) {
        return null;
    }

    @Override
    public AuthResponse signup(UserDTO request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidEmailException("El correo electrónico ya está en uso."));

        User newUser = User.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .lastLogin(LocalDateTime.now())
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(newUser);
        Authentication auth =  new UsernamePasswordAuthenticationToken(newUser.getEmail(), newUser.getPassword());
        String jwt = jwtUtil.generateToken(auth);
        return  AuthResponse.builder()
                .message("Registro correcto")
                .title("Bienvenido")
                .user(UserMapper.toDTO(newUser))
                .jwt(jwt).build();

    }

    @Override
    public void createPasswordResetToken(String email) {

    }

    @Override
    public void resetPassword(String token, String newPassword) {

    }
}
