package com.soracel.library_system.service;

import com.soracel.library_system.domain.FineStatus;
import com.soracel.library_system.domain.FineType;
import com.soracel.library_system.payload.dto.FineDTO;
import com.soracel.library_system.payload.request.CreateFineRequest;
import com.soracel.library_system.payload.request.WaiveFineRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.payments.CheckoutSession;

import java.math.BigDecimal;
import java.util.List;

public interface FineService {

    FineDTO createFine(CreateFineRequest request);

    CheckoutSession checkoutFine(Long fineId, BigDecimal amount, String fineReason);

    FineDTO waiveFine(WaiveFineRequest request);

    List<FineDTO> getUserFines(FineStatus fineStatus, FineType fineType);

    PageResponse<FineDTO>  getAllFines(FineStatus  fineStatus, FineType fineType, Long userId, int page, int pageSize);

}
