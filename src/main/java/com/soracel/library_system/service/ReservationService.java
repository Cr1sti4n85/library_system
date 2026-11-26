package com.soracel.library_system.service;

import com.soracel.library_system.domain.ReservationStatus;
import com.soracel.library_system.payload.dto.ReservationDTO;
import com.soracel.library_system.payload.request.ReservationRequest;
import com.soracel.library_system.payload.request.ReservationSearchRequest;
import com.soracel.library_system.payload.response.PageResponse;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest request);

    ReservationDTO createReservationForUser(ReservationRequest request, Long userId);

    ReservationDTO cancelReservation(Long reservationId);

    void expireReservation();

    void makeReservationAvailable();

    ReservationDTO fulfillReservation(Long reservationId);

    PageResponse<ReservationDTO> getMyReservations(ReservationStatus status,
                                                   Boolean activeOnly, int page, int size,
                                                   String sortBy, String sortDirection);

    PageResponse<ReservationDTO> searchReservations(Long userId, ReservationStatus status,
                                                    Boolean activeOnly, int page, int size,
                                                    Long bookId, String sortBy, String sortDirection);

}
