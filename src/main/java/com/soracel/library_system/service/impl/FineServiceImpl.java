package com.soracel.library_system.service.impl;

import com.soracel.library_system.common.pagination.PaginationUtil;
import com.soracel.library_system.domain.FineStatus;
import com.soracel.library_system.domain.FineType;
import com.soracel.library_system.exception.FineException;
import com.soracel.library_system.exception.NotFoundException;
import com.soracel.library_system.mapper.FineMapper;
import com.soracel.library_system.model.Fine;
import com.soracel.library_system.model.User;
import com.soracel.library_system.payload.dto.FineDTO;
import com.soracel.library_system.payload.request.WaiveFineRequest;
import com.soracel.library_system.payload.response.PageResponse;
import com.soracel.library_system.payments.CheckoutSession;
import com.soracel.library_system.payments.PaymentGateway;
import com.soracel.library_system.repository.FineRepository;
import com.soracel.library_system.service.FineService;
import com.soracel.library_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository  fineRepository;
    private final PaymentGateway paymentGateway;
    private final UserService  userService;

    @Override
    public CheckoutSession payFine(Long fineId) {
        Fine existingFine = fineRepository.findById(fineId)
                .orElseThrow(() -> new NotFoundException("No existe multa con ese id"));
        if (existingFine.getStatus().equals(FineStatus.PAID) ||  existingFine.getStatus().equals(FineStatus.WAIVED)) {
            throw new FineException("La multa ya fue pagada o anulada");
        }

        return paymentGateway.createFineCheckoutSession(existingFine);
    }

    @Override
    public FineDTO waiveFine(WaiveFineRequest request) {

        Fine fine = fineRepository
                .findById(request.getFineId())
                .orElseThrow(() -> new FineException("El id proporcionado no es correcto."));

        //check if already waived or paid
        if (fine.getStatus().equals(FineStatus.PAID) || fine.getStatus().equals(FineStatus.WAIVED)) {
            throw new FineException("La multa ya fue pagada o anulada");
        }

        User currentAdmin = userService.getCurrentUserEntity();
        fine.waive(currentAdmin, request.getReason());

        Fine savedFine = fineRepository.save(fine);

        return FineMapper.toDTO(savedFine);
    }

    @Override
    public List<FineDTO> getUserFines(FineStatus fineStatus, FineType fineType) {
        User currentUser = userService.getCurrentUserEntity();
        List<Fine> fines;

        if (fineStatus != null && fineType != null) {
            fines = findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getStatus().equals(fineStatus) && f.getFineType().equals(fineType))
                    .toList();
        } else if (fineStatus != null) {
            fines = findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getStatus().equals(fineStatus))
                    .toList();
        } else if (fineType != null) {
            fines = findByUserId(currentUser.getId()).stream()
                    .filter(f -> f.getFineType().equals(fineType))
                    .toList();
        } else {
            fines = findByUserId(currentUser.getId());
        }
        return fines.stream().map(FineMapper::toDTO).toList();
    }

    @Override
    public PageResponse<FineDTO> getAllFines(FineStatus fineStatus,
                                             FineType fineType,
                                             Long userId,
                                             int page,
                                             int pageSize) {
        Pageable pageable = PaginationUtil.createPageable(page, pageSize, "createdAt", "DESC");

        Page<Fine> finePage = fineRepository.findAllWithFilters(userId, fineStatus, fineType, pageable);
        return PaginationUtil.convertToPageResponse(finePage, FineMapper::toDTO);
    }

    @Override
    public List<Fine> findByUserId(Long id) {
        return fineRepository.findByUserId(id);
    }
}
