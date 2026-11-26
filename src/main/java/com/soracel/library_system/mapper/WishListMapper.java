package com.soracel.library_system.mapper;

import com.soracel.library_system.model.WishList;
import com.soracel.library_system.payload.dto.WishListDTO;

public class WishListMapper {
    public static WishListDTO toDTO(WishList wishList) {
        if (wishList == null) {
            return null;
        }
        return WishListDTO.builder()
                .id(wishList.getId())
                .userId(wishList.getUser().getId())
                .userFullName(wishList.getUser().getFullName())
                .book(BookMapper.toDTO(wishList.getBook()))
                .createdAt(wishList.getCreatedAt())
                .updatedAt(wishList.getUpdatedAt())
                .build();
    }
}
