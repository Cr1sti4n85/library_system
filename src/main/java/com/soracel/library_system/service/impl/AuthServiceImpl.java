package com.soracel.library_system.service.impl;

import com.soracel.library_system.configurations.JwtUtil;
import com.soracel.library_system.domain.UserRole;
import com.soracel.library_system.exception.ExpiredTokenException;
import com.soracel.library_system.exception.InvalidCredentialsException;
import com.soracel.library_system.exception.InvalidEmailException;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.UserMapper;
import com.soracel.library_system.model.PasswordResetToken;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.payload.response.AuthResponse;
import com.soracel.library_system.repository.PasswordResetTokenRepository;
import com.soracel.library_system.repository.UserRepository;
import com.soracel.library_system.service.AuthService;
import com.soracel.library_system.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil  jwtUtil;
    private final CustomUserServiceImpl customUserService;
    private final PasswordResetTokenRepository  passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    public AuthResponse signup(UserDTO request) {
        Optional<User> user = userRepository
                .findByEmail(request.getEmail());

        if  (user.isPresent()) {
            throw  new InvalidEmailException("Email ya existe");
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .fullName(request.getFullName())
                .lastLogin(LocalDateTime.now())
                .role(UserRole.ROLE_USER)
                .build();
        userRepository.save(newUser);
        return  AuthResponse.builder()
                .message("Registro correcto")
                .title("Bienvenido")
                .user(UserMapper.toDTO(newUser))
                .build();
    }

    public AuthResponse login(String username, String password) {
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.generateToken(authentication);

        User user = userRepository
                .findByEmail(username)
                .orElseThrow(() -> new InvalidEmailException("Email inválido"));

        //update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .title("Inicio de sesión exitoso")
                .message("Bienvenido" + username)
                .jwt(token)
                .user(UserMapper.toDTO(user))
                .build();
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new InvalidCredentialsException("Las credenciales de acceso no son válidas");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Transactional
    public void createPasswordResetToken(String email) {
        String frontEndURL = "http://localhost:5173/";
        User user = userRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException("Email no existe"));
        String temporaryToken = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(temporaryToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        passwordResetTokenRepository.save(resetToken);
        String resetLink = frontEndURL + temporaryToken;
        String subject = "Restablecimiento de contraseña";
        String body = "Has enviado una solicitud para restablecer tu contraseña. Usa el siguiente enlace, el cual" +
                "será válido durante los próximos 10 minutos: " + resetLink;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        PasswordResetToken foundResetToken =  passwordResetTokenRepository
                .findByToken(resetToken)
                .orElseThrow(() -> new NotFoundException("El token proporcionado no es válido"));

        if (foundResetToken.isTokenExpired()){
            //remove token from db
            passwordResetTokenRepository.delete(foundResetToken);
            throw new ExpiredTokenException("El token proporcionado ha expirado");
        }

        User user = foundResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(foundResetToken);

    }
}
