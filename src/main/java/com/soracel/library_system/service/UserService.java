package com.soracel.library_system.service;

import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDTO getCurrentUser();
    User getCurrentUserEntity();
    List<UserDTO> getAllUsers();
    String getEmailFromAuth();
    User getUserById(Long id);
    void updateUserCustomerId(Long id, String customerId);
    User findByCustomerId(String customerId);
}
