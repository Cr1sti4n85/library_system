package com.soracel.library_system.service;

import com.soracel.library_system.payload.dto.ReservationDTO;
import com.soracel.library_system.payload.request.ReservationRequest;
import com.soracel.library_system.payload.request.ReservationSearchRequest;
import com.soracel.library_system.payload.response.PageResponse;

public interface ReservationService {

    ReservationDTO createReservation(ReservationRequest request);

    ReservationDTO createReservationForUser(ReservationRequest request, Long userId);

    ReservationDTO cancelReservation(Long reservationId);

    ReservationDTO fulfillReservation(Long reservationId);

    PageResponse<ReservationDTO> getMyReservations(ReservationSearchRequest request);

    PageResponse<ReservationDTO> searchReservations(ReservationSearchRequest request);

}
