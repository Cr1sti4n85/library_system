package com.soracel.library_system.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class WishListDTO {

    private Long id;
    private Long userId;
    private String userFullName;
    private BookDTO book;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
