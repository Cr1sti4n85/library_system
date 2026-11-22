package com.soracel.library_system.service.impl;

import com.soracel.library_system.domain.FineStatus;
import com.soracel.library_system.domain.FineType;
import com.soracel.library_system.mapper.FineMapper;
import com.soracel.library_system.model.BookLoan;
import com.soracel.library_system.model.Fine;
import com.soracel.library_system.payload.dto.FineDTO;
import com.soracel.library_system.payload.request.WaiveFineRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.repository.FineRepository;
import com.soracel.library_system.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository  fineRepository;


    @Override
    public FineDTO waiveFine(WaiveFineRequest request) {
        return null;
    }

    @Override
    public List<FineDTO> getUserFines(FineStatus fineStatus, FineType fineType) {
        return List.of();
    }

    @Override
    public PageResponse<FineDTO> getAllFines(FineStatus fineStatus, FineType fineType, Long userId, int page, int pageSize) {
        return null;
    }
}
