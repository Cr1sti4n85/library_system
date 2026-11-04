package com.soracel.library_system.mapper;

import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDTO toDTO(User user){
        if (user == null){
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .lastLogin(user.getLastLogin())
                .role(user.getRole())
                .build();
    }

    public static List<UserDTO> toDTOList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    public static Set<UserDTO> toDTOSet(Set<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public static User toEntity(UserDTO userDTO){
        if (userDTO == null){
            return null;
        }
        return User.builder()
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .phone(userDTO.getPhone())
                .fullName(userDTO.getFullName())
                .role(userDTO.getRole())
                .build();


    }
}
