package com.soracel.library_system.controller;

import com.soracel.library_system.model.Fine;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fines")
public class FineController {

    private final FineService finseService;

    @PostMapping("/{fineId}")
    public ResponseEntity<CheckoutSession> payFine(@PathVariable Long fineId) {
        CheckoutSession session = finseService.payFine(fineId);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }
}
