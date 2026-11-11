package com.soracel.library_system.service.impl;


import com.soracel.library_system.exception.InvalidCredentialsException;
import com.soracel.library_system.exception.InvalidEmailException;
import com.soracel.library_system.model.User;
import com.soracel.library_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@AllArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Las credenciales de acceso no son válidas"));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().toString());

        Collection<? extends GrantedAuthority>  authorities = Collections.singletonList(grantedAuthority);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );
    }
}
