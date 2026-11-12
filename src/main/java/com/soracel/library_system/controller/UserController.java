package com.soracel.library_system.controller;

import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.UserDTO;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile() {

        return ResponseEntity.ok(userService.getCurrentUser());
    }

}
