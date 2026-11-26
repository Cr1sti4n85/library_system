package com.soracel.library_system.controller;

import com.soracel.library_system.model.WishList;
import com.soracel.library_system.payload.dto.WishListDTO;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishListController {

    private final WishListService  wishListService;

    @PostMapping("/{bookId}")
    public ResponseEntity<WishListDTO> createWishList(@PathVariable Long bookId) {
        WishListDTO dto = wishListService.addToWishList(bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<WishListDTO>> getWishList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        PageResponse<WishListDTO> wishListDTOPage = wishListService.getMyWishList(page, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(wishListDTOPage);

    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<WishListDTO> removeFromWishList(@PathVariable Long bookId) {
        wishListService.removeFromWishList(bookId);
        return ResponseEntity.noContent().build();
    }

}
