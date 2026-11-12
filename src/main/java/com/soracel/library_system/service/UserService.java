package com.soracel.library_system.service;

import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO getCurrentUser();
    User getCurrentUserEntity();
    List<UserDTO> getAllUsers();
    String getEmailFromAuth();
}
