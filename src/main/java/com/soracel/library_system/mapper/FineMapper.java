package com.soracel.library_system.mapper;

import com.soracel.library_system.model.Fine;
import com.soracel.library_system.payload.dto.FineDTO;

public class FineMapper {

    public static FineDTO toDTO(Fine fine) {
        if  (fine == null) {
            return null;
        }

        FineDTO.FineDTOBuilder builder = FineDTO.builder()
                                        .id(fine.getId())
                                        .bookLoanId(fine.getBookLoan().getId())
                                        .bookTitle(fine.getBookLoan().getBook().getTitle())
                                        .bookIsbn(fine.getBookLoan().getBook().getIsbn())
                                        .userId(fine.getUser().getId())
                                        .username(fine.getUser().getFullName())
                                        .userEmail(fine.getUser().getEmail())
                                        .type(fine.getFineType())
                                        .amount(fine.getAmount())
                                        .status(fine.getStatus())
                                        .reason(fine.getFineReason())
                                        .notes(fine.getNotes());

        if (fine.getWaivedBy() != null) {
            builder.waivedByUserId(fine.getWaivedBy().getId());

        }

        builder.waivedAt(fine.getWaivedAt())
                .waiverReason(fine.getWaiverReason())
                .paidAt(fine.getPaidAt());

        return builder.build();

    }
}
