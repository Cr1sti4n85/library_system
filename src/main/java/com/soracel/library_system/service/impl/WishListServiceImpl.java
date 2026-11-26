package com.soracel.library_system.service.impl;

import com.soracel.library_system.common.pagination.PaginationUtil;
import com.soracel.library_system.exception.BookException;
import com.soracel.library_system.exception.WishListException;
import com.soracel.library_system.mapper.WishListMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.User;
import com.soracel.library_system.model.WishList;
import com.soracel.library_system.payload.dto.WishListDTO;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.BookRepository;
import com.soracel.library_system.repository.WishListRepository;
import com.soracel.library_system.service.UserService;
import com.soracel.library_system.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final UserService  userService;
    private final BookRepository  bookRepository;

    @Override
    public WishListDTO addToWishList(Long bookId) {
        User user = userService.getCurrentUserEntity();

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookException("El id indicado no pertenece a ningún libro"));

        boolean existsInWishList = wishListRepository.existsByUserIdAndBookId(user.getId(), book.getId());

        if (existsInWishList) {
            throw new WishListException("El libro ya existe en la wishlist");
        }

        WishList newWishList = WishList.builder()
                .user(user)
                .book(book)
                .build();
        return WishListMapper.toDTO(wishListRepository.save(newWishList));
    }

    @Transactional
    public void removeFromWishList(Long bookId) {
        User user = userService.getCurrentUserEntity();

        if (!wishListRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new WishListException("El libro no forma parte de tu wishlist");
        }
        wishListRepository.deleteByUserIdAndBookId(user.getId(), bookId);
    }


    @Override
    public PageResponse<WishListDTO> getMyWishList(Integer page, Integer pageSize) {
        User user = userService.getCurrentUserEntity();

        Pageable pageable = PaginationUtil.createPageable(page, pageSize, "createdAt", "DESC");
        Page<WishList> wishList = wishListRepository.findByUserId(user.getId(), pageable);
        return PaginationUtil.convertToPageResponse(wishList, WishListMapper::toDTO);
    }
}




