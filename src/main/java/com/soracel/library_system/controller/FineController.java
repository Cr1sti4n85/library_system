package com.soracel.library_system.controller;

import com.soracel.library_system.domain.FineStatus;
import com.soracel.library_system.domain.FineType;
import com.soracel.library_system.model.Fine;
import com.soracel.library_system.payload.dto.FineDTO;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    @PostMapping("/{fineId}")
    public ResponseEntity<CheckoutSession> payFine(@PathVariable Long fineId) {
        CheckoutSession session = fineService.payFine(fineId);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping
    public ResponseEntity<List<FineDTO>> getMyFines(@RequestParam(required = false) FineStatus status,
                                                 @RequestParam(required = false)FineType type) {

        List<FineDTO> dtoList = fineService.getUserFines(status, type);
        return ResponseEntity.status(HttpStatus.OK).body(dtoList);
    }
}
