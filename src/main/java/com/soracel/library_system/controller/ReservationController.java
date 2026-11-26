package com.soracel.library_system.controller;

import com.soracel.library_system.domain.ReservationStatus;
import com.soracel.library_system.payload.dto.ReservationDTO;
import com.soracel.library_system.payload.request.ReservationRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationDTO dto = reservationService.createReservation(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReservationDTO>> searchReservations(
                                                    @RequestParam(required = false)ReservationStatus status,
                                                    @RequestParam(required = false) Boolean activeOnly,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(defaultValue = "reservedAt") String sortBy,
                                                    @RequestParam(defaultValue = "DESC") String sortDirection

    ) {

        PageResponse<ReservationDTO> pageResponse = reservationService.getMyReservations(
                                                                                        status,
                                                                                        activeOnly,
                                                                                        page,
                                                                                        size,
                                                                                        sortBy,
                                                                                        sortDirection);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        ReservationDTO dto = reservationService.cancelReservation(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationDTO> fulfillReservation(@PathVariable Long id) {
        ReservationDTO dto = reservationService.fulfillReservation(id);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

}
