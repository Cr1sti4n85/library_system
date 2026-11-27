package com.soracel.library_system.service.impl;

import com.soracel.library_system.common.pagination.PaginationUtil;
import com.soracel.library_system.domain.ReservationStatus;
import com.soracel.library_system.domain.UserRole;
import com.soracel.library_system.exception.*;
import com.soracel.library_system.mapper.ReservationMapper;
import com.soracel.library_system.model.Book;
import com.soracel.library_system.model.Reservation;
import com.soracel.library_system.model.Subscription;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.ReservationDTO;
import com.soracel.library_system.payload.request.CheckoutRequest;
import com.soracel.library_system.payload.request.ReservationRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.repository.BookRepository;
import com.soracel.library_system.repository.ReservationRepository;
import com.soracel.library_system.service.BookLoanService;
import com.soracel.library_system.service.ReservationService;
import com.soracel.library_system.service.SubscriptionService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookLoanService bookLoanService;
    private final UserService  userService;
    private final BookRepository bookRepository;
    private final SubscriptionService  subscriptionService;

    @Override
    public ReservationDTO createReservation(ReservationRequest request) {
        User user = userService.getCurrentUserEntity();
        return createReservationForUser(request, user.getId());
    }

    @Transactional
    public ReservationDTO createReservationForUser(ReservationRequest request, Long userId) {

        User user = userService.getUserById(userId);

        //check if user has active subscription
        Subscription activeSub = user.getSubscriptions()
                                .stream()
                                .filter(sub -> sub.getIsActive() == true)
                                .findFirst()
                                .orElseThrow(() -> new SubscriptionException("No tiene suscripciones activas."));

        //check if book was already borrowed
        boolean alreadyHasLoan = bookLoanService.checkLoanExistsForUser(userId, request.getBookId());

        if (alreadyHasLoan) {
            throw new LoanException("El usuario ya tiene préstamo activo asociado al libro.");
        }

        //check book exists
        Book existingBook = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookException("El libro no está disponible"));

        if (reservationRepository.hasActiveReservation(userId, request.getBookId())){
            throw new ReservationException("Ya tienes reservado este libro");
        }

        //check availability of book
        if (existingBook.getAvailableCopies() > 0){
            throw new ReservationException("El libro ya está disponible");
        }

        //check reservation limit based on plan
        long activeReservations = reservationRepository.countActiveReservationsByUser(userId);
        long reservationLimit = activeSub.getPlan().getMaxReservationLimit();

        if (activeReservations == reservationLimit){
            throw new ReservationException("Has llegado al límite de tus reservaciones: " + reservationLimit);
        }

        //create reservation
        Reservation newReservation = Reservation.builder()
                .user(user)
                .book(existingBook)
                .reservedAt(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        //queue
        int pendingCount = reservationRepository.countPendingReservationsByBook(existingBook.getId());

        newReservation.setQueuePosition(pendingCount + 1);

        existingBook.addReservation(newReservation);

        bookRepository.save(existingBook);

        return ReservationMapper.toDTO(newReservation);
    }

    @Transactional
    public ReservationDTO cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("El id de reservación proporcionado no es correcto"));

        //verify user owns the reservation
        User user = userService.getCurrentUserEntity();
        if(!reservation.getUser().getId().equals(user.getId())
                && user.getRole() != UserRole.ROLE_ADMIN){
            throw new ForbiddenUserException("No tienes permiso para cancelar esta reservación.");
        }

        if (!reservation.canBeCancelled()){
            throw new ReservationException("La reservación expiró, ya se canceló o ya obtuviste el préstamo asociado.");
        }

        //get book to reorganize its reservation queue
        Long bookId = reservation.getBook().getId();

        reservation.setStatus(ReservationStatus.CANCELED);
        reservation.setCancelledAt(LocalDateTime.now());

        reorganizeReservationQueue(bookId);

        Reservation  savedReservation = reservationRepository.save(reservation);

        return ReservationMapper.toDTO(savedReservation);
    }

    @Override
    public void expireReservation() {

        List<Reservation> reservations = reservationRepository.findExpiredReservations(LocalDateTime.now());

        if (!reservations.isEmpty()){
            for(Reservation reservation : reservations){

                    reservation.setStatus(ReservationStatus.EXPIRED);
                    reservation.setQueuePosition(null);
                    reorganizeReservationQueue(reservation.getBook().getId());
                    reservationRepository.save(reservation);
            }

        }
    }

    @Override
    public void makeReservationAvailable() {
        List<Book> books = bookRepository.hasAvailableBookCopies();
        if (!books.isEmpty()){
            int counter= 0;
            for(Book book : books){
                List<Reservation> reservationList = book.getReservations()
                        .stream()
                        .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                        .toList();
                if (reservationList.size() > book.getAvailableCopies()){
                    for(Reservation reservation : reservationList){
                        updateAvailability(reservation);
                        reorganizeReservationQueue(book.getId());
                        counter++;
                        if (counter == book.getAvailableCopies()) break;
                    }

                } else {
                    for(Reservation reservation : reservationList){
                        updateAvailability(reservation);
                        reorganizeReservationQueue(book.getId());
                    }

                }
            }
            //TODO enviar notificacion el cliente por email y el link para realizar el préstamo

        }

    }

    private void updateAvailability(Reservation reservation) {
        reservation.setStatus(ReservationStatus.AVAILABLE);
        reservation.setAvailableAt(LocalDateTime.now());
        reservation.setQueuePosition(null);
        reservation.setAvailableUntil(LocalDateTime.now().plusHours(1));
        reservationRepository.save(reservation);
    }

    @Transactional
    public ReservationDTO fulfillReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("No existe reserva con ese id"));

        if (reservation.getBook().getAvailableCopies() == 0){
            throw new ReservationException("El reservamiento no está disponible para retiro actualmente");
        }

        reservation.setStatus(ReservationStatus.FULFILLED);
        reservation.setFulfilledAt(LocalDateTime.now());
        reservation.setQueuePosition(null);

        Reservation savedReservation = reservationRepository.save(reservation);

        reorganizeReservationQueue(savedReservation.getBook().getId());

        //CREATE CHECKOUT
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        checkoutRequest.setBookId(reservation.getBook().getId());
        checkoutRequest.setNotes("Préstamo exitoso mediante proceso de reserva");

        bookLoanService.checkoutBookForUser(reservation.getUser().getId(), checkoutRequest);
        return ReservationMapper.toDTO(savedReservation);
    }

    @Override
    public PageResponse<ReservationDTO> getMyReservations(ReservationStatus status, Boolean activeOnly,
                                                          int page, int size, String sortBy, String sortDirection) {
        User user = userService.getCurrentUserEntity();
        return searchReservations(user.getId(), status, activeOnly, page, size, null, sortBy, sortDirection);
    }

    @Override
    public PageResponse<ReservationDTO> searchReservations(Long userId, ReservationStatus status,
                                                           Boolean activeOnly, int page, int size,
                                                           Long bookId, String sortBy, String sortDirection) {

        Pageable pageable = PaginationUtil.createPageable(page, size, sortBy, sortDirection);

        Page<Reservation> reservationPage = reservationRepository
                                            .searchReservationWithFilters(
                                                    userId,
                                                    bookId,
                                                    status,
                                                    activeOnly != null && activeOnly,
                                                    pageable);
        return PaginationUtil.convertToPageResponse(reservationPage, ReservationMapper::toDTO);
    }

    private void reorganizeReservationQueue(Long bookId) {
        List<Reservation> reservations = reservationRepository
                                        .findByBookIdAndStatusOrderByQueuePositionAsc(
                                                        bookId,
                                                        ReservationStatus.PENDING);
        if (!reservations.isEmpty()){
            for (int i = 0; i < reservations.size(); i++) {
                Reservation reservation = reservations.get(i);
                reservation.setQueuePosition(i + 1);
            }
            reservationRepository.saveAll(reservations);
        }

    }
}
