package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.WishListDTO;
import com.soracel.library_system.payload.response.PageResponse;

public interface WishListService {

    WishListDTO addToWishList(Long bookId);

    void removeFromWishList(Long bookId);

    PageResponse<WishListDTO> getMyWishList(Integer page, Integer pageSize);
}
