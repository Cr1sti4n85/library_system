package com.soracel.library_system.service.impl;

import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.UserMapper;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.repository.UserRepository;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new BadCredentialsException("Usuario no autenticado");
        }
        String email = authentication.getName();
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuario no existe"));

        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        return UserMapper.toDTOList(users);
    }
}
